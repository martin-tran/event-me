var config = {
  apiKey: "AIzaSyAw2C8gsyMDjav7afSIPzfZzBaMfkvzRGM",
  authDomain: "eventmi-c39d5.firebaseapp.com",
  databaseURL: "https://eventmi-c39d5.firebaseio.com",
  storageBucket: "gs://eventmi-c39d5.appspot.com/"
};
firebase.initializeApp(config);

var database = firebase.database().ref();
var auth = firebase.auth();
var provider = new firebase.auth.GoogleAuthProvider();

var user = null;

document.getElementById('signin').addEventListener('click', googleAuthentication);
function googleAuthentication(event) {
  if (user === null) {
    console.log('logging in...');
    firebase.auth().signInWithPopup(provider).then(function(result) {
      user = {
        token: result.credential.accessToken,
        user: result.user
      };
      document.getElementById('signin').innerHTML = 'Sign Out <i class="fa fa-google"></i>';
      console.log(user['user']);
    }).catch(function(error) {
      console.log(error.code);
      console.log(error.message);
      console.log(error.email);
      console.log(errro.credential);
    });
  } else {
    console.log('logging out');
    firebase.auth().signOut().then(function() {
      user = null;
      document.getElementById('signin').innerHTML = 'Sign In <i class="fa fa-google"></i>';
    }).catch(function(error) {
      // An error happened.
    });
  }
}