param(
    [Parameter(Mandatory = $true)][string]$Texte,
    [int]$Occurrence = 0,
    [string]$Dump = "C:\Users\dell\Desktop\tp14_client_soap_kotlin\docs\logs\ui_courant.xml"
)

$adb = "C:\Users\dell\AppData\Local\Android\Sdk\platform-tools\adb.exe"

& $adb shell uiautomator dump /sdcard/ui.xml | Out-Null
& $adb shell cat /sdcard/ui.xml > $Dump
$xml = Get-Content $Dump -Raw

$motif = '<node[^>]*text="' + [regex]::Escape($Texte) + '"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"'
$trouves = [regex]::Matches($xml, $motif)
if ($trouves.Count -le $Occurrence) {
    Write-Output "INTROUVABLE:$Texte"
    exit 1
}

$m = $trouves[$Occurrence]
$x = [int](([int]$m.Groups[1].Value + [int]$m.Groups[3].Value) / 2)
$y = [int](([int]$m.Groups[2].Value + [int]$m.Groups[4].Value) / 2)
& $adb shell input tap $x $y | Out-Null
Write-Output "TAP:$Texte@$x,$y"
