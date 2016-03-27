function autocorrect() {
	var userinput = document.getElementById("input").value;
	if (userinput.length >= 1) {
		var inputstr = JSON.stringify(userinput);
    var postParameters = {input : inputstr};

    $.post("/home", postParameters, function(responseJSON) {
     	var responseObject = JSON.parse(responseJSON);
     	var rOne = responseObject.home;
     	var div = document.getElementById("results");
     	div.innerHTML = rOne;
   	});
  }
}

function autocorrect1() {
  var userinput = document.getElementById("input1").value;
  if (userinput.length >= 1) {
    var inputstr = JSON.stringify(userinput);
    var postParameters = {input : inputstr};

    $.post("/home", postParameters, function(responseJSON) {
      var responseObject = JSON.parse(responseJSON);
      var rOne = responseObject.home;
      var div = document.getElementById("results1");
      div.innerHTML = rOne;
    });
  }
}

$("#addActor").click(function() {
  var userInput = document.getElementById("aAdd").value;
  if (userInput.length == 0) {
    alert("Please enter an actor name!");
  } else {
    var inputstr = JSON.stringify(userInput);
    var postParameters = {name : inputstr};
    $.post("/updateA", postParameters, function(responseJSON) {
      var responseObject = JSON.parse(responseJSON);
      console.log(responseObject.response);
    });
  }
});

$("#addFilm").click(function() {
  var userInput = document.getElementById("fAdd").value;
  if (userInput.length == 0) {
    alert("Please enter an actor name!");
  } else {
    var inputstr = JSON.stringify(userInput);
    var postParameters = {name : inputstr};
    var response = "response";
    $.post("/updateF", postParameters, function(responseJSON) {
      var responseObject = JSON.parse(responseJSON);
      alert(responseObject.response);
    });
  }
});

$("#addAToF").click(function() {
  var actor = document.getElementById("aMod").value;
  var film = document.getElementById("fMod").value;
  if ((actor.length == 0) || (film.length == 0)) {
    alert("Please enter values!");
  } else {
    var postParameters = {a : actor, f: film};

    $.post("/updateAFA", postParameters, function(responseJSON) {
      var responseObject = JSON.parse(responseJSON);
      alert(responseObject.response);
    });
  }
});

$("#removeAFromF").click(function() {
  var actor = document.getElementById("aMod").value;
  var film = document.getElementById("fMod").value;
  if ((actor.length == 0) || (film.length == 0)) {
    alert("Please enter values!");
  } else {
    var postParameters = {a : actor, f: film};

    $.post("/updateAFR", postParameters, function(responseJSON) {
      var responseObject = JSON.parse(responseJSON);
      alert(responseObject.response);
    });
  }
});
