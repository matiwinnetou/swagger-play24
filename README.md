# swagger-play24
This is a 'direct' move of Swagger support for Play (2.3) to (2.4).
This project does not try to be too clever and has a very few changes comparing to original code

# Spec
This library is based on Swagger Spec 1.2

# Installation
to your build.sbt add

```
libraryDependencies += "pl.matisoft" %% "swagger-play24" % "1.0"
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

# Disable
- just remove the library from classpath or 
