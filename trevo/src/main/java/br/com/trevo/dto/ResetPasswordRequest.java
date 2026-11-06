package br.com.trevo.dto;

public record ResetPasswordRequest(String token, String password) {}
