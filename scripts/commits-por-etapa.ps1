# ObservaAcao - commits por etapa (modulo)
# Uso:
#   .\scripts\commits-por-etapa.ps1           # executa todas as etapas
#   .\scripts\commits-por-etapa.ps1 -Etapa 3 # so a etapa 3
#   .\scripts\commits-por-etapa.ps1 -DryRun    # simula sem commitar

param(
    [int]$Etapa = 0,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

function Test-GitRepo {
    if (-not (Test-Path ".git")) {
        throw "Execute na raiz do repositorio git."
    }
}

function Invoke-Etapa {
    param(
        [int]$Numero,
        [string]$Titulo,
        [string[]]$Caminhos,
        [string]$Mensagem
    )

    if ($Etapa -ne 0 -and $Etapa -ne $Numero) { return }

    Write-Host ""
    Write-Host "========== ETAPA $Numero - $Titulo ==========" -ForegroundColor Cyan

    $existentes = @()
    foreach ($c in $Caminhos) {
        if (Test-Path $c) { $existentes += $c }
        else { Write-Host "  [skip] nao encontrado: $c" -ForegroundColor DarkYellow }
    }

    if ($existentes.Count -eq 0) {
        Write-Host "  Nada para adicionar." -ForegroundColor DarkYellow
        return
    }

    foreach ($c in $existentes) {
        Write-Host "  + $c"
    }

    if ($DryRun) {
        Write-Host "  [dry-run] git add ..." -ForegroundColor DarkGray
        Write-Host "  [dry-run] git commit -m `"$Mensagem`"" -ForegroundColor DarkGray
        return
    }

    git add @existentes
    $status = git diff --cached --name-only
    if (-not $status) {
        Write-Host "  Nada staged (ja commitado ou vazio)." -ForegroundColor DarkYellow
        return
    }

    git commit -m $Mensagem
    if ($LASTEXITCODE -ne 0) { throw "Commit da etapa $Numero falhou." }
    Write-Host "  OK commit $($Numero)" -ForegroundColor Green
}

Test-GitRepo

Write-Host "Repositorio: $Root" -ForegroundColor Gray
if ($DryRun) { Write-Host "Modo: DRY-RUN (nao commita)" -ForegroundColor Yellow }

$etapas = @(
    @{
        Numero = 1
        Titulo = "Backend scaffold e infra"
        Caminhos = @(
            "backend/pom.xml",
            "backend/src/main/resources/application.properties",
            "backend/src/main/java/org/boligon/ObservacaoApplication.java",
            "backend/src/main/java/org/boligon/enums",
            "backend/src/main/java/org/boligon/exception",
            "backend/src/main/java/org/boligon/config",
            "backend/src/main/java/org/boligon/configbanco"
        )
        Mensagem = "feat(backend): scaffold Spring Boot com enums, excecoes e configuracao base"
    },
    @{
        Numero = 2
        Titulo = "Backend modulo auth"
        Caminhos = @("backend/src/main/java/org/boligon/auth")
        Mensagem = "feat(backend): modulo de autenticacao e registro de usuarios"
    },
    @{
        Numero = 3
        Titulo = "Backend modulo solicitacao"
        Caminhos = @(
            "backend/src/main/java/org/boligon/solicitacao",
            "backend/src/main/java/org/boligon/shared"
        )
        Mensagem = "feat(backend): modulo de solicitacoes, fila e auditoria"
    },
    @{
        Numero = 4
        Titulo = "Backend historico e enums REST"
        Caminhos = @(
            "backend/src/main/java/org/boligon/historico",
            "backend/src/main/java/org/boligon/web"
        )
        Mensagem = "feat(backend): historico de status e endpoints de enums"
    },
    @{
        Numero = 5
        Titulo = "Frontend scaffold Vite React"
        Caminhos = @(
            "frontend/package.json",
            "frontend/package-lock.json",
            "frontend/vite.config.js",
            "frontend/index.html",
            "frontend/.gitignore",
            "frontend/src/main.jsx"
        )
        Mensagem = "feat(frontend): scaffold React com Vite e Tailwind"
    },
    @{
        Numero = 6
        Titulo = "Frontend API e autenticacao"
        Caminhos = @(
            "frontend/src/api",
            "frontend/src/context",
            "frontend/src/utils",
            "frontend/src/App.jsx"
        )
        Mensagem = "feat(frontend): cliente API, contexto de auth e rotas protegidas"
    },
    @{
        Numero = 7
        Titulo = "Frontend componentes compartilhados"
        Caminhos = @(
            "frontend/src/components/Modal.jsx",
            "frontend/src/components/PriorityBadge.jsx",
            "frontend/src/components/StatusBadge.jsx",
            "frontend/src/components/TimelineHistorico.jsx",
            "frontend/src/components/NavActions.jsx"
        )
        Mensagem = "feat(frontend): componentes de badge, modal e timeline"
    },
    @{
        Numero = 8
        Titulo = "Frontend telas login e cidadao"
        Caminhos = @(
            "frontend/src/pages/LoginPage.jsx",
            "frontend/src/pages/CidadaoPage.jsx",
            "frontend/src/pages/NovaSolicitacaoPage.jsx",
            "frontend/src/pages/MinhasSolicitacoesPage.jsx"
        )
        Mensagem = "feat(frontend): telas de login, hub cidadao e nova solicitacao"
    },
    @{
        Numero = 9
        Titulo = "Frontend telas gestor e acompanhar"
        Caminhos = @(
            "frontend/src/pages/GestorPage.jsx",
            "frontend/src/pages/AcompanharPage.jsx"
        )
        Mensagem = "feat(frontend): painel do gestor e consulta por protocolo"
    },
    @{
        Numero = 10
        Titulo = "Frontend layout sidebar e icones"
        Caminhos = @(
            "frontend/src/components/SidebarLayout.jsx",
            "frontend/src/components/PublicLayout.jsx",
            "frontend/src/components/LogoObserva.jsx",
            "frontend/src/components/NavIcon.jsx",
            "frontend/src/components/AppIcon.jsx",
            "frontend/src/index.css"
        )
        Mensagem = "feat(frontend): layout com sidebar e paleta inspirada no ORCISPAR"
    },
    @{
        Numero = 11
        Titulo = "Frontend assets publicos"
        Caminhos = @(
            "frontend/public/bg.svg",
            "frontend/public/logo.svg",
            "frontend/public/favicon.svg",
            "frontend/public/icons.svg",
            "frontend/public/teste.png",
            "frontend/src/assets"
        )
        Mensagem = "chore(frontend): assets de logo, fundo e imagem do login"
    },
    @{
        Numero = 12
        Titulo = "Scripts e gitignore"
        Caminhos = @(
            "scripts",
            ".gitignore"
        )
        Mensagem = "chore: script de commits por etapa e ajustes no gitignore"
    }
)

foreach ($e in $etapas) {
    Invoke-Etapa -Numero $e.Numero -Titulo $e.Titulo -Caminhos $e.Caminhos -Mensagem $e.Mensagem
}

Write-Host ""
Write-Host "========== RESUMO ==========" -ForegroundColor Cyan
git log --oneline -15
Write-Host ""
git status --short
