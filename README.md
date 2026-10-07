# Guxa

Guxa é um aplicativo Android nativo de comunicação e comunidades.

## Visão

- Servidores e comunidades
- Canais de texto
- Canais de voz
- Videochamadas
- Compartilhamento de tela
- Câmera e microfone
- Amigos e mensagens privadas
- Menções e notificações push
- Cargos e permissões
- Arquivos e mídia
- Bots e integrações
- Eventos
- Moderação
- Chamadas em segundo plano
- Picture-in-picture
- WebRTC + SFU para mídia

## Build

O workflow `.github/workflows/build-apk.yml` gera `app-debug.apk` automaticamente no GitHub Actions.

## Google Login

O app já usa Android Credential Manager + Google Identity.

Adicione o secret do repositório:

`GUXA_GOOGLE_WEB_CLIENT_ID`

Esse valor é o **Web Client ID** usado como server client ID no fluxo Google.

### SHA-1

O OAuth Android usa a combinação do package name + SHA-1 da assinatura. Para uma assinatura local, use:

`gradle signingReport`

Quando houver uma keystore de release própria, o SHA-1 da release também deverá ser cadastrado no Google Cloud.

### Client secret

**Não coloque o client secret no APK.** APKs podem ser inspecionados. Se o backend precisar do secret, ele deve existir somente no servidor.

## Estado

A base inicial já é um APK nativo, não uma aplicação web empacotada:

- build automatizado
- Google Login preparado
- permissões de câmera/microfone/notificações declaradas
- canais de notificação para menções, chamadas, mensagens e servidores
- FCM service preparado
- UI inicial de chats, servidores, notificações e calls
- manifesto preparado para câmera, microfone e MediaProjection

As chamadas reais serão implementadas com WebRTC + SFU e a camada de MediaProjection para screen share.
