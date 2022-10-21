# vm tester

## Tester tool for .vm templates

Creates result files in <code>data/result</code> directory from velocity templates in <code>data/vm</code> directory, for each of the <code>.json</code> files loaded into velocity context from <code>data/request</code> directory

<code>data/global.json</code> contains global variables in context shared between all requests

## Development

Visual Studio Code + JDK (1.8 and 17 both work) + extension <a href="https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack">vscjava.vscode-java-pack</a>

## Configuration

<code>app.properties</code>:

AddSchemaLinkToXML - set to true (default) if you want to have resulted xml files to contain link to provided <code>schema.xsd</code>

## Validation

<code>PDF</code> - json validation should be out of box in vs code

<code>XML</code> - vs code extension <a href="https://marketplace.visualstudio.com/items?itemName=redhat.vscode-xml">redhat.vscode-xml</a>

## Tutorial

1. Have JDK >= 1.8 installed on pc

2. Open root directory in Visual Studio Code

3. Install java extension <a href="https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack">vscjava.vscode-java-pack</a>

4. Fill <code>data/global.json</code> with required global variables

5. Edit <code>data/schema.xsd</code> with appropriate xsd schema

6. Place vm templates in <code>data/vm/</code> directory

7. Create necessary <code>.json</code> files in <code>data/request/</code> directory filled with <code>scenarioDto.applicantAnswers</code> values from final <code>getNextStep</code> requests

8. Run application with <code>F5</code>

9. Resulted files will be generated in <code>data/result/</code> directory

10. <code>PDF</code> files will be generated as <code>.json</code> files which can be validated by opening the file in Visual Studio Code

11. <code>XML</code> files by default will have a link to <code>data/schema.xsd</code> and can be validated by opening the file in Visual Studio Code with installed <a href="https://marketplace.visualstudio.com/items?itemName=redhat.vscode-xml">redhat.vscode-xml</a> extension

12. To remove link from resulted <code>XML</code> files edit <code>app.properties</code> file in root directory and change <code>AddSchemaLinkToXML</code> value to <code>false</code>
