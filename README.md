# vm tester

Tester tool for .vm templates

Creates result files from velocity templates in <code>data/vm</code> directory, for each of the \*.json file loaded into velocity context from <code>data/request</code> directory

<code>data/global.json</code> contains global variables for context shared between all requests

## Configuration

<code>app.properties</code>:

AddSchemaLinkToXML - set to true (default) if you want to have resulted xml files to contain link to provided <code>schema.xsd</code>
