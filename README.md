# HumanNameParse.java

Java port Author: Bruno P. Kinoshita
(Steve Ash is just publishing this fork to Maven Central to make it available for some of my OSS projects; Im not contributing anything to this parser)

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
    <version>0.1</version>
  </dependency>
</dependencies>
```

```
Name object = new Name("Sérgio Vieira de Mello");
HumanNameParserParser parser = new HumanNameParserParser(object);
String firstName = parser.getFirst();
String nicknames = parser.getNicknames();
// ...
```