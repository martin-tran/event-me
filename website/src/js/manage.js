var config = {
  apiKey: "AIzaSyAw2C8gsyMDjav7afSIPzfZzBaMfkvzRGM",
  authDomain: "eventmi-c39d5.firebaseapp.com",
  databaseURL: "https://eventmi-c39d5.firebaseio.com",
  storageBucket: "gs://eventmi-c39d5.appspot.com/"
};
firebase.initializeApp(config);

var database = firebase.database().ref();
var provider = new firebase.auth.GoogleAuthProvider();

var setUser = false;
var buttonsHTML = document.getElementById('buttons').innerHTML.split('\n');
var submitButtonHTML = buttonsHTML[1];
var closeButtonHTML = buttonsHTML[2];

var defaultFormHTML = document.getElementById('formModalStatus').innerHTML;
function validTitle(title) {
  return title !== '';
}

function validNumber(number) {
  return number !== '' && !isNaN(number);
}

function validLocation(location) {
  return location.length == 2 && (validNumber(location[0]) && validNumber(location[1]));
}

document.getElementById('newEvent').addEventListener('click', openNewEventForm);
function openNewEventForm(event) {
  event.preventDefault();

  if (!setUser) {
      document.getElementById('formModalStatus').innerHTML = '<p>Please login above to make an event</p>';
      document.getElementById('buttons').innerHTML = closeButtonHTML;
    $('#formModal').modal();
  } else {
      document.getElementById('formModalStatus').innerHTML = defaultFormHTML;
      document.getElementById('buttons').innerHTML = submitButtonHTML + '\n' + closeButtonHTML;
      document.getElementById('submit').addEventListener('click', addEventToFirebase);
  }
    $('#formModal').modal();
}

function addEventToFirebase(event) {
  event.preventDefault();
  var titl = document.getElementById('inputTitle').value;
  var proxim = document.getElementById('inputProximity').value;
  var loc = document.getElementById('inputLocation').value.split(',');
  var disc = document.getElementById('inputDiscord').value;
  var site = document.getElementById('inputWebsite').value;
  var place = document.getElementById('inputPlaceName').value;
  var dets = document.getElementById('inputDetails').value;

    if (!validTitle(titl) || !validNumber(proxim) || !validLocation(loc)) {
	if (!validTitle(titl)) {
	    if (!document.getElementById('inputTitleLabel').innerHTML.endsWith('</font>')) {
		document.getElementById('inputTitleLabel').innerHTML += ' <font color="red">Invalid title!</font>';
	    }
	}
	if (!validNumber(proxim)) {
	    if (!document.getElementById('inputProximityLabel').innerHTML.endsWith('</font>')) {
		document.getElementById('inputProximityLabel').innerHTML += ' <font color="red">Invalid proximity!</font>';
	    }
	}
	if (!validLocation(loc)) {
	    if (!document.getElementById('inputLocationLabel').innerHTML.endsWith('</font>')) {
		document.getElementById('inputLocationLabel').innerHTML += ' <font color="red">Invalid location!</font>';
	    }
	}
	$('#formModal').modal();
	
    } else {
	document.getElementById('formModalStatus').innerHTML = '<p>Event created!</p>';
	document.getElementById('buttons').innerHTML = closeButtonHTML;

	firebase.database().ref('events').push({
            title: titl,
            proximity: Number(proxim),
            latitude: Number(loc[0]),
            longitude: Number(loc[1]),
            discord: disc,
            website: site,
            place_name: place,
            details: dets,
            uid: firebase.auth().currentUser.uid

	});
    }
    $('#formModal').modal();

}

document.getElementById('navSignin').addEventListener('click', googleAuthentication);
function googleAuthentication(event) {
  if (!setUser) {
    setUser = true;
    console.log('logging in...');
    firebase.auth().signInWithPopup(provider).then(function(result) {
      // result.credential.accessToken;
      // result.user;
      // firebase.auth().currentUser.uid;
      document.getElementById('navSignin').innerHTML = 'Sign Out <i class="fa fa-google"></i>';
    }).catch(function(error) {
      console.log(error.code);
      console.log(error.message);
      console.log(error.email);
      console.log(errro.credential);
    });
  } else {
    console.log('logging out');
    firebase.auth().signOut().then(function() {
      setUser = false;
      document.getElementById('navSignin').innerHTML = 'Sign In <i class="fa fa-google"></i>';
    }).catch(function(error) {
      // An error happened.
    });
  }
}

document.getElementById('refreshEvents').addEventListener('click', getEventsFromFirebase);
function getEventsFromFirebase(event) {
  event.preventDefault();
  firebase.database().ref('events').once('value', function(snapshot) {
    if (snapshot.exists()) {
      document.getElementById('eventContainer').innerHTML = '';
      var i = 0;
      snapshot.forEach(function(data) {
	var val = data.val();
        if (val.uid === firebase.auth().currentUser.uid) {

	  var content = '';
          content += '<div class="list-group-item list-group-item-action flex-column align-items-start" onclick="viewEventDetails(this)" id=event' + i + '>';
          content += '<div class="d-flex w-100 justify-content-between">';
          content += `<h5 class="mb-1">${val.title}</h5>`;
          content += `<small class="text-muted">${val.place_name}</small>`;
          content += '</div>';
          content += `<small class="text-muted">Proximity: ${val.proximity}</small>`;
          content += `<p class="mb-1">${val.details}</p>`;
          content += '</div>';
          document.getElementById('eventContainer').innerHTML += content;
        }
	i += 1;
      });
    } else {
      document.getElementById('eventContainer').innerHTML = '<p>No events at this time</p>';
    }
  })
}

function viewEventDetails(event) {
  firebase.database().ref('events').once('value', function(snapshot) {
    if (snapshot.exists()) {
      document.getElementById('detailContainer').innerHTML = '';
      var i = 0;
      snapshot.forEach(function(data) {
	var val = data.val();
          if (val.uid === firebase.auth().currentUser.uid && ('event' + i) == event.id) {
	      
	  var content = '';
          content += '<div class="list-group-item list-group-item-action flex-column align-items-start onclick="viewEventDetails(this)" id=detail' + i + '>';
          content += '<div class="d-flex w-100 justify-content-between">';
          content += `<h5 class="mb-1">${val.title}</h5>`;
          content += `<p class="text-muted">${val.place_name}</p>`;
          content += '</div>';
	  content += `<p class="text-muted">Details: ${val.details}</p>`;
          content += `<p class="text-muted">Proximity: ${val.proximity}</p>`;
	  content += `<p class="text-muted">Website: ${val.website}</p>`;
	  content += `<p class="text-muted">Discord: ${val.discord}</p>`;
          content += `<p class="mb-1">${val.details}</p>`;
          content += '</div>';
          content += '<button type="button" class="btn btn-default" id="delete">Delete Event</button>'
	  document.getElementById('detailContainer').innerHTML += content;
	}
	i += 1;   
      });
    }
  })
}
