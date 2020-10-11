resource "aws_sqs_queue" "trello_webhook_queue" {
  name                        = "cossmass-trello-webkhook-queue"
}