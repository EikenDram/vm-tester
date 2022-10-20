# vm tester

## Tester tool for .vm templates

Creates result files in <code>data/result</code> directory from velocity templates in <code>data/vm</code> directory, for each of the \*.json files loaded into velocity context from <code>data/request</code> directory

<code>data/global.json</code> contains global variables in context shared between all requests

## Development

Visual Studio Code + JDK (1.8 and 17 both work) + extension <a href="https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack">vscjava.vscode-java-pack</a>

## Configuration

<code>app.properties</code>:

AddSchemaLinkToXML - set to true (default) if you want to have resulted xml files to contain link to provided <code>schema.xsd</code>

## Validation

<code>PDF</code> - json validation should be out of box in vs code

<code>XML</code> - vs code extension <a href="https://marketplace.visualstudio.com/items?itemName=redhat.vscode-xml">redhat.vscode-xml</a>
