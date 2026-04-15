terraform {
  required_providers {
    vault = {
      source  = "hashicorp/vault"
      version = "~> 5.0"
    }
  }
}

provider "vault" {
  address = var.vault_address
  token   = var.vault_token
}

# Variables


variable "vault_address" {
  description = "Vault server URL"
  default     = "http://127.0.0.1:8200"
}

variable "vault_token" {
  description = "Vault root/auth token"
  sensitive   = true
}

variable "db_username" {
  sensitive = true
}

variable "db_password" {
  sensitive = true
}

variable "google_client_id" {
  sensitive = true
}

variable "google_client_secret" {
  sensitive = true
}

variable "jwt_secret" {
  sensitive = true
}

variable "twilio_account_sid" {
  sensitive = true
}

variable "twilio_auth_token" {
  sensitive = true
}

variable "twilio_phone_number" {
  sensitive = true
}

# Resource 1: Database Credentials

resource "vault_kv_secret_v2" "db_cred" {
  mount               = "secret"
  name                = "hackathon-app/db"
  delete_all_versions = true

  data_json = jsonencode({
    "spring.datasource.username" = var.db_username
    "spring.datasource.password" = var.db_password
  })
}

# Resource 2: OAuth Credentials

resource "vault_kv_secret_v2" "oauth_secret" {
  mount               = "secret"
  name                = "hackathon-app/oauth"
  delete_all_versions = true

  data_json = jsonencode({
    "google-client-id"     = var.google_client_id
    "google-client-secret" = var.google_client_secret
  })
}

# Resource 3: JWT Secret

resource "vault_kv_secret_v2" "jwt_secret" {
  mount               = "secret"
  name                = "hackathon-app/jwt"
  delete_all_versions = true

  data_json = jsonencode({
    "jwt.secret" = var.jwt_secret
  })
}

# Resource 4: Twilio Credentials

resource "vault_kv_secret_v2" "twilio" {
  mount               = "secret"
  name                = "hackathon-app/twilio"
  delete_all_versions = true

  data_json = jsonencode({
    "twilio.account-sid"  = var.twilio_account_sid
    "twilio.auth-token"   = var.twilio_auth_token
    "twilio.phone-number" = var.twilio_phone_number
  })
}
resource "kubernetes_secret" "ai_secret" {
  metadata {
    name = "openai-credentials"
  }

  data = {
    "fast2sms.api.key" ="vTGCuNfBWRHwJrtPKiAFExQIjlMzUdSYabX0Zqg87k1psO543cD9FZ1jqWCnyArQaSUc0VG3bsB67MhJ"
  }
}
