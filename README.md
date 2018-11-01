# PasippMicroservice

Digitaler Anhang zur Masterarbeit
"Integration von Prolog-Modulen in eine Microservice-Architektur"
von Fabian Stolz am Karlsruher Institut für Technologie.

Nutzungs- und Installationsanweisungen sind in der Ausarbeitung
der Masterarbeit enthalten.

## Inhalt

Dieses Repository enthält:
* Arity/Prolog32: Dieser Ordner enthält eine Arity/Prolog32-Distribution. Arity/Prolog32 ist eine Implementierung der Prolog-Programmiersprache, lauffähig unter Windows 7 und 10 (andere Versionen wurden im Rahmen der Masterarbeit nicht getestet)
* Arity/Prolog32 Dokumentation: Dieser Ordner enthält die Dokumentation von Arity/Prolog32, kopiert von http://www.peter-gabel.com/ (mittlerweile dort nicht mehr verfügbar)
* prologService.jar: eine ausführbare jar-Datei, die den im Rahmen der Masterarbeit erstellten Microservice enthält
* petrianalyzer-res: Dieser Ordner enthält die Konfigurations-Datei für den externen Service _PetriAnalyzer_ (siehe weiter unten)
* API32.env: diese Datei enthält Umgebungsvariablen für den Arity/Prolg32 Interpreter. Diese Datei sollte in den _bin_-Ordner der Arity/Prolog32-Installation kopiert werden (C:\PASIPP\Arity\bin, falls den Anweisungen in der Ausarbeitung gefolgt wurde).

## Abhängigkeiten
* Der Microservice benötigt zum Funktionieren die Quelldateien des Programms _PASIPP_.
* Falls der Microservice Ablaufmodelle aus dem HORUS Business Modeler verarbeiten soll, wird dazu der Dienst _PetriAnalyzer_ benötigt, verfügbar unter https://github.com/cgrossde/PetriAnalyzer

## Lizenz
Die Lizenz ist in der Datei LICENSE zu finden. Die Unterordner _ArityProlog32_ und _ArityProlog32 Dokumentation_ stehen unter einer anderen Lizens. Diese ist in dem jeweiligen Ordner in der README- oder der LICENSE-Datei zu finden.