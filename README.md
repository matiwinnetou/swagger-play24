# swagger-play24
This is a 'direct' move of Swagger support for Play (2.3) to (2.4)
This project does not try to be too clever and has a very few changes comparing to original code.

Library version | Play Version  | Swagger Spec version |  Scala Version |
--------------- | ------------- | -------------------- | -------------- |
    1.1         |   2.4         |          1.2         | 2.10
    1.2         |   2.4         |          1.2         | 2.10  
    1.3         |   2.4         |          1.2         | 2.10, 2.11

# Installation
add to your build.sbt

```
libraryDependencies += "pl.matisoft" %% "swagger-play24" % "1.1"
```

and then
add as an example to your routes file:

```
GET     /api-docs                                         @pl.matisoft.swagger.ApiHelpController.getResources
```

application.conf:
```
api.version=1.0.0
swagger.api.basepath=http://localhost:9000
```

# application.conf - config options
```
api.version (String) - version of API | default: "beta"
swagger.api.basepath (String) - base url | default: "http://localhost:9000"
swagger.filter (String) - classname of swagger filter | default: empty
```

# Usage
Once library is hooked up it is possible to add swagger annotation to classes and methods. The exact documentation and example is available on a swagger web page.

# Scala Support
- library is available for Scala 2.10 and Scala 2.11

# Java Support
- library is supported for Play Java but not yet tested... :)

# Notes
Library automatically instantiates two classes (eagerly) upon startup:
- SwaggerPlugin via SwaggerPluginProvider
- ApiHelpController

# Disable
- just remove the library from classpath - it will then not register and instantiate any classes
