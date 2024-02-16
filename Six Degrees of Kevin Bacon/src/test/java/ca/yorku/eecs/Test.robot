*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds

*** Test Cases ***
addActorPass
    Create Session  localhost   http://localhost:8080
    ${headers}= Create Dictionary    Content-Type=application/json
    ${params}=  Create Dictionary   name=Denzel Washington  actorId=nm1001213
    ${response}=    Put Request localhost    /api/v1/addActor    data=${params}  headers=${headers}
    Should Be Equal As Strings  ${resp.status_code}   200

AddActorFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Ayaan
    ${resp}=    Put Request    localhost    /api/v1/AddActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

AddMoviePass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Parasite    movieId=nm7001453
    ${resp}=    Put Request    localhost    /api/v1/AddMovie    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

AddMovieFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Iron Man    
    ${resp}=    Put Request    localhost    /api/v1/AddMovie    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

AddRelationshipPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001231    movieId=nm7001453
    ${resp}=    Put Request    localhost    /api/v1/AddRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

AddRelationshipFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001231    movieId=nm7001453
    ${resp}=    Put Request    localhost    /api/v1/AddRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

GetActorPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001231
    ${resp}=    Get Request    localhost    /api/v1/GetActor    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Value    ${resp.json()}    "actorId": "nm1001231","name": "Ramy Youssef"

GetActorFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001231
    ${resp}=    Get Request    localhost    /api/v1/GetActor    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

GetMoviePass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=nm1111891
    ${resp}=    Get Request    localhost    /api/v1/GetMovie    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Value    ${resp.json()}    "movieId": "nm1111891", "name": "Groundhog Day"

GetMovieFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=nm1111891
    ${resp}=    Get Request    localhost    /api/v1/GetMovie    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

HasRelationshipPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001231    movieId=nm1251671
    ${resp}=    Get Request    localhost    /api/v1/HasRelationship    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Value    ${resp.json()}   hasRelationship: "true"

HasRelationshipFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm1001231    movieId=nm1251671
    ${resp}=    Get Request    localhost    /api/v1/HasRelationship    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

ComputeBaconNumberPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   actorId=nm1001231
    ${resp}=    Get Request    localhost    /api/v1/ComputeBaconNumber    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Value    ${resp.json()}    baconNumber: 3

ComputeBaconNumberFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   actorId=nm1001231
    ${resp}=    Get Request    localhost    /api/v1/ComputeBaconNumber    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

ComputeBaconPathPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   actorId=nm1991271
    ${resp}=    Get Request    localhost    /api/v1/ComputeBaconPath    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Value    ${resp.json()}   ComputeBaconPath = "baconPath": ["nm1991271","nm9112231","nm9191136","nm9894331","nm0000102"]

ComputeBaconPathFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   actorId=nm1991271
    ${resp}=    Get Request    localhost    /api/v1/ComputeBaconPath    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

AddLocationPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Toronto    locationId=lc10000
    ${resp}=    Put Request    localhost    /api/v1/AddLocation    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

AddLocationFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=toronto
    ${resp}=    Put Request    localhost    /api/v1/AddLocation    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

AddFilmedInPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=nm7001453    locationId=lc10000
    ${resp}=    Put Request    localhost    /api/v1/AddFilmedIn    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

AddFilmedInFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=nm7001453
    ${resp}=    Put Request    localhost    /api/v1/AddFilmedIn    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

HasFilmedInPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=nm7001453    locationId=lc10000
    ${resp}=    Get Request    localhost    /api/v1/HasFilmedIn   json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Value    ${resp.json()}   hasFilmedIn: "false"

HasFilmedInFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=nm7001453    locationId=lc10000
    ${resp}=    Get Request    localhost    /api/v1/HasFilmedIn   json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

GetMovieThisYearPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     year = 2012
    ${resp}=    Get Request    localhost    /api/v1/GetMovieThisYear   json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Value    ${resp.json()}   "listMovies": ["The Dark Knight Rises","Avengers Assemble","The Amazing Spider-Man","The Hunt","Skyfall"]

GetMovieThisYearFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary     year = 20000  
    ${resp}=    Get Request    localhost    /api/v1/GetMovieThisYear   json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

GetActorsStarredTogetherPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000375    actorId=nm0749263
    ${resp}=    Get Request    localhost    /api/v1/GetActorsStarredTogether   json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Value    ${resp.json()}   "listMovies": ["Avengers Assemble","Iron-Man 3","Zodiac","Captain Marvel"]

GetActorsStarredTogetherFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000375    actorId=nm0749263
    ${resp}=    Get Request    localhost    /api/v1/GetActorsStarredTogether   json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

GetMovieThisLocationPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    locationId=lc10000
    ${resp}=    Get Request    localhost    /api/v1/GetMovieThisLocation   json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Value    ${resp.json()}   "listMovies": ["Avengers Assemble","Iron-Man 3","Zodiac","Captain Marvel"]

GetMovieThisLocationFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    locationId=lc10000
    ${resp}=    Get Request    localhost    /api/v1/GetMovieThisLocation   json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400