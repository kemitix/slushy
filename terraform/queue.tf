resource "aws_sqs_queue" "slushy_webhook_queue" {
  name = "slushy-webhook-${var.environment}-queue"
}

resource "aws_iam_role" "slushy_webhook_role" {
  name = "slushy-webhook-${var.environment}-role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "apigateway.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

data "template_file" "slushy_webhook_policy_template" {
  template = file("policies/api-gateway-permission.json")

  vars = {
    sqs_arn   = aws_sqs_queue.slushy_webhook_queue.arn
  }
}

resource "aws_iam_policy" "slushy_webhook_policy" {
  name = "slushy-webhook-${var.environment}-policy"

  policy = data.template_file.slushy_webhook_policy_template.rendered
}


resource "aws_iam_role_policy_attachment" "slushy_webhook_policy_attachment" {
  role       =  aws_iam_role.slushy_webhook_role.name
  policy_arn =  aws_iam_policy.slushy_webhook_policy.arn
}

resource "aws_api_gateway_rest_api" "slushy_webhook_rest_api" {
  name        = "slushy-webhook-${var.environment}-rest-api"
  description = "POST records to SQS queue"
}

resource "aws_api_gateway_resource" "slushy_webhook_resource" {
  rest_api_id = aws_api_gateway_rest_api.slushy_webhook_rest_api.id
  parent_id   = aws_api_gateway_rest_api.slushy_webhook_rest_api.root_resource_id
  path_part   = "webhook"
}

resource "aws_api_gateway_method" "slushy_webhook_method" {
  rest_api_id   = aws_api_gateway_rest_api.slushy_webhook_rest_api.id
  resource_id   = aws_api_gateway_resource.slushy_webhook_resource.id
  http_method   = "POST"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "slushy_webhook_sqs_integration" {
  rest_api_id             = aws_api_gateway_rest_api.slushy_webhook_rest_api.id
  resource_id             = aws_api_gateway_resource.slushy_webhook_resource.id
  http_method             = aws_api_gateway_method.slushy_webhook_method.http_method
  type                    = "AWS"
  integration_http_method = "POST"
  credentials             = aws_iam_role.slushy_webhook_role.arn
  uri                     = "arn:aws:apigateway:${var.region}:sqs:path/${aws_sqs_queue.slushy_webhook_queue.name}"

  request_parameters = {
    "integration.request.header.Content-Type" = "'application/x-www-form-urlencoded'"
  }

  # Request Template for passing Method, Body, QueryParameters and PathParams to SQS messages
  request_templates = {
    "application/json" = <<EOF
Action=SendMessage&MessageBody={
  "method": "$context.httpMethod",
  "body-json" : $input.json('$'),
  "queryParams": {
    #foreach($param in $input.params().querystring.keySet())
    "$param": "$util.escapeJavaScript($input.params().querystring.get($param))" #if($foreach.hasNext),#end
  #end
  },
  "pathParams": {
    #foreach($param in $input.params().path.keySet())
    "$param": "$util.escapeJavaScript($input.params().path.get($param))" #if($foreach.hasNext),#end
    #end
  }
}"
EOF
  }

  depends_on = [
    aws_iam_role_policy_attachment.slushy_webhook_policy_attachment
  ]
}

resource "aws_api_gateway_method_response" "slushy_webhook_method_response" {
  rest_api_id = aws_api_gateway_rest_api.slushy_webhook_rest_api.id
  resource_id = aws_api_gateway_resource.slushy_webhook_resource.id
  http_method = aws_api_gateway_method.slushy_webhook_method.http_method
  status_code = 200
}

resource "aws_api_gateway_integration_response" "slushy_webhook_integration_response" {
  rest_api_id       = aws_api_gateway_rest_api.slushy_webhook_rest_api.id
  resource_id       = aws_api_gateway_resource.slushy_webhook_resource.id
  http_method       = aws_api_gateway_method.slushy_webhook_method.http_method
  status_code       = aws_api_gateway_method_response.slushy_webhook_method_response.status_code
  selection_pattern = "^2[0-9][0-9]" // regex pattern for any 200 message that comes back from SQS

  depends_on = [
    aws_api_gateway_integration.slushy_webhook_sqs_integration
  ]
}

resource "aws_api_gateway_deployment" "slushy_webhook_deployment" {
  rest_api_id = aws_api_gateway_rest_api.slushy_webhook_rest_api.id
  stage_name  = var.environment

  depends_on = [
    aws_api_gateway_integration.slushy_webhook_sqs_integration,
  ]

  # Redeploy when there are new updates
  triggers = {
    redeployment = sha1(join(",", list(
    jsonencode(aws_api_gateway_integration.slushy_webhook_sqs_integration),
    )))
  }

  lifecycle {
    create_before_destroy = true
  }
}