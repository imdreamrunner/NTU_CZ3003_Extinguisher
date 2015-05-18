/*
 * 1. generate operators
 * 2. generate observers
 * 3. generate incidents
 */


// 1
var op1 = { "_id" : "operator1", "password" : "123" };
var op2 = { "_id" : "operator2", "password" : "1234", "priviledges" : [ "read", "write" ] };
var op3 = { "_id" : "operator3", "password" : "1234", "priviledges" : [ "read" ] };
var op4 = { "_id" : "operator4", "password" : "xxxxpassword", "priviledges" : [ "read", "write", "delete" ] };
var op5 = { "_id" : "operator5", "password" : "1234", "priviledges" : [ "read", "operator.create", "observer.create" ] };

db.operator.insert(op1)
db.operator.insert(op2)
db.operator.insert(op3)
db.operator.insert(op4)
db.operator.insert(op5)

// 2
var obs1 = { "_id" : ObjectId("53245643bc8ac22720fc88b3"), "url" : "http://localhost:8080/test1", "tags" : [ "fire", "haze" ], "showAllInfo" : false };
db.observer.insert(obs1);

// 3
function genSimpInc(){
    var result = {
        incident:{
        },
        operator:{username:"operator1", password:"1234"}
    };
}

var inc1 = {
	"_id" : ObjectId("532aabab84ae49a73cf7df71"),
	"reporter" : {
		"name" : "reporter1"
	},
	"startTime" : NumberLong("1395303331903"),
	"isLatest" : true,
	"level" : 1,
	"timeStamp" : NumberLong("1395305387582"),
	"remark" : "some remarks",
	"location" : {
		
	},
	"parent" : null,
	"type" : "fire",
	"isValid" : true,
	"completeTime" : null,
	"operator" : "operator1",
	"initialId" : null
};
db.incident.insert(inc1);
