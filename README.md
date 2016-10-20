bancika-gui-framework is my personal collection of GUI and non-GUI components and utilities I use regularly. The GUI-related components are packaged as <b>swing-framework</b> and everything else that doesn't depend on Swing is packaged as <b>app-framework</b>.

## app-framework

- <b>Simple MQ</b> - asynchronous message dispatching mechanism.
- <b>JAR Scanner</b> - scans folders and JAR files searching for classes.
- <b>Undo/Redo Manager</b> - mechanism for undo / redo operations.
- <b>Configuration Manager</b> - simple utility for persisting application configuration to a file.
- <b>Update Manager</b> - adds auto-update ability to your application. Scans the update descriptor file from a web page and compares with local version. All new features can be shown in the dialog.

## swing-framework

The Swing package contains several Java components:

- <b>Auto-fit Table</b> - JTable that adjusts column width based on the contents.
- <b>Object Table</b> - JTable that displays an array of objects where each object is displayed as a row and it's field are placed in columns. It's possible to configure which fields will be shown and whether they should be editable or not.
- <b>Ruler Scroll Pane</b> - JScrollPane descendant that shows horizontal and vertical rulers in both inches and centimeters. It also features auto-scroll button that shows a thumbnail of viewport component and allows for easy navigation.
- <b>Button Dialog</b> - JDialog with convenience methods.
- <b>Memory Bar</b> - small panel that shows memory consumption and runs the garbage collector when clicked.
- <b>Table Export Utility</b> - exports JTable contents to HTML, CSV, Excel and PNG
- <b>Drawing Export Utility</b> - uses Java2D Graphics to export image to CSV, PDF and to print it.

and several other components.
