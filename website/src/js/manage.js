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
      snapshot.forEach(function(data) {
        var val = data.val();
        if (val.uid === firebase.auth().currentUser.uid) {
          var content = '';
          content += '<div class="list-group-item list-group-item-action flex-column align-items-start">';
          content += '<div class="d-flex w-100 justify-content-between">';
          content += `<h5 class="mb-1">${val.title}</h5>`;
          content += `<small class="text-muted">${val.place_name}</small>`;
          content += '</div>';
          content += `<small class="text-muted">Proximity: ${val.proximity}</small>`;
          content += `<p class="mb-1">${val.details}</p>`;
          content += '</div>';
          document.getElementById('eventContainer').innerHTML += content;
        }
      });
    } else {
      document.getElementById('eventContainer').innerHTML = '<p>No events at this time</p>';
    }
  })
}