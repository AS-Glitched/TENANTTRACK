$env:JAVA_HOME = (Resolve-Path "$PSScriptRoot\.jdk").Path
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "JAVA_HOME = $env:JAVA_HOME"
java -version

& "$PSScriptRoot\mvnw.cmd" spring-boot:run
