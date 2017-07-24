# HumanNameParse.java

Java port Author: Bruno P. Kinoshita
(Steve Ash is just publishing this fork to Maven Central to make it available for some of my OSS projects; Im not contributing anything to this parser)

Also it should be noted that this is a relatively naive name parser; and 
its implementation is highly unoptimized. I (steve ash) do not recommend using this in any production situations where
performance is sensitive. I am only publishing it on maven central to include as a 
sample parser to demonstrate the integration of a name parser into Syngen. I did make a number
of changes to just make it usable, but I would love it if someone would publish a competitive 
open-source parser

Original library Author: Jason Priem jason@jasonpriem.com (credits go to him)
Original library Author Website: http://jasonpriem.com/human-name-parse

License: [MIT](http://www.opensource.org/licenses/mit-license.php)

## Description
Takes human names of arbitrary complexity and various wacky formats like:

* J. Walter Weatherman 
* de la Cruz, Ana M. 
* James C. ('Jimmy') O'Dell, Jr.

and parses out the:

* leading initial (Like "J." in "J. Walter Weatherman")
* first name (or first initial in a name like 'R. Crumb')
* nicknames (like "Jimmy" in "James C. ('Jimmy') O'Dell, Jr.")
* middle names
* last name (including compound ones like "van der Sar' and "Ortega y Gasset"), and
* suffix (like 'Jr.', 'III')

## Usage
This is available on maven central as:

```
<dependencies>
  <dependency>
    <groupId>com.github.steveash.hnp</groupId>
    <artifactId>human-name-parser</artifactId>
    <version>0.2</version>
  </dependency>
</dependencies>
```

```
HumanNameParser parser = new HumanNameParser();
ParsedName name = parser.parse("Sérgio Vieira de Mello")
for (int i = 0; i < name.size(); i++) {
  name.getToken(i);  // the i-th token
  name.getLabel(i);  // the label for that token
}
// or access the name as segmented into first, middle, last, etc.
SegmentedName segName = name.toSegmented();

String firstName = segName.getFirst(); // Sergio
String nicknames = segName.getLast();  // de Mello (note its not smart it doesn't know name cultural practices) 
// ...
```