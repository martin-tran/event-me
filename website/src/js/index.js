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

function validTitle(title) {
  return title !== '';
}

function validNumber(number) {
  return number !== '' && !isNaN(number);
}

function validLocation(location) {
  return location.length == 2 && (validNumber(location[0]) && validNumber(location[1]));
}

document.getElementById('eventForm').addEventListener('submit', addEventToFirebase);
function addEventToFirebase(event) {
  event.preventDefault();
  var titl = document.getElementById('inputTitle').value;
  var proxim = document.getElementById('inputProximity').value;
  var loc = document.getElementById('inputLocation').value.split(',');
  var disc = document.getElementById('inputDiscord').value;
  var site = document.getElementById('inputWebsite').value;
  var place = document.getElementById('inputPlaceName').value;
  var dets = document.getElementById('inputDetails').value;
  
  if (!setUser) {
    document.getElementById('formModalStatus').innerHTML = '<p>Please login above to make an event</p>';
    $('#formModal').modal();
  } else {
    if (!validTitle(titl) || !validNumber(proxim) || !validLocation(loc)) {
      document.getElementById('formModalStatus').innerHTML = '<p>Please check values.</p>';
    } else {
      document.getElementById('formModalStatus').innerHTML = '<p>Event created!</p>';

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
      document.getElementById('navCreate').innerHTML = '<a class="nav-link" href="manage.html" target="_self">Manage Events</a>';
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
      document.getElementById('navCreate').innerHTML = '<a class="nav-link" href="#createevent" target="_self">Create Event</a>';
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
      snapshot.forEach(function(data) {
        var val = data.val();
        var content = '';
        content += '<div class="list-group-item list-group-item-action flex-column align-items-start">';
        content += '<div class="d-flex w-100 justify-content-between">';
        content += `<h5 class="mb-1">${val.title}</h5>`;
        content += `<small class="text-muted">${val.place_name}</small>`;
        content += '</div>';
        content += `<div><small class="text-muted">Location: ${val.latitude.toFixed(5)}, ${val.longitude.toFixed(5)}</small></div>`;
        content += `<div><small class="text-muted">Proximity: ${val.proximity}</small></div>`;
        content += `<div><small class="text-muted">Website: ${val.website}</small></div>`;
        content += `<p class="mb-1">${val.details}</p>`;
        content += '</div>';
        document.getElementById('eventContainer').innerHTML += content;
      });
    } else {
      document.getElementById('eventContainer').innerHTML = '<p>No events at this time</p>';
    }
  })
}