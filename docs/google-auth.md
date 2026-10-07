# Guxa — Google OAuth

## O que você precisa criar

Para Android, cadastre um cliente OAuth do tipo Android no Google Cloud com:

- package: `com.felpz.guxa`
- SHA-1 da assinatura usada pela build

Também crie/tenha um cliente OAuth do tipo Web. O app usa o Web Client ID como `serverClientId` no Credential Manager.

## SHA-1

Debug/local:

`gradle signingReport`

Para release, use a SHA-1 da keystore de release. Se publicar pela Play, também considere a chave de assinatura usada pelo Google Play.

## Secrets

GitHub Actions:

`GUXA_GOOGLE_WEB_CLIENT_ID`

O Web Client ID pode ser incluído no APK porque ele identifica o cliente público; ele não é uma senha.

**Nunca coloque o Google OAuth client secret no APK.**

Se o backend precisar do secret, configure-o somente no backend, por exemplo:

`GUXA_GOOGLE_CLIENT_SECRET`

O backend deve validar o ID token e/ou fazer a troca OAuth conforme a arquitetura escolhida.

## Fluxo do Guxa

```
Android
  │
  ├── Credential Manager
  │
  ├── Google Account
  │
  └── Google ID Token
          │
          ▼
      Guxa Backend
          │
          ├── valida token
          ├── cria/atualiza usuário
          └── cria sessão Guxa
```

O backend ainda precisa ser implementado antes de considerar o login de produção concluído.
