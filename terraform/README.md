# Terraform Infrastructure

Record state using **yadm** on the `master` worktree.

## dev

```bash
terraform plan -state=dev/state
terraform apply -state=dev/state
```

## live

```bash
terraform plan -state=live/state -var-file live.tfvars
terraform apply -state=live/state -var-file live.tfvars
```
