'use strict';

const Filter = require('bad-words');
const badWordsFilter = new Filter();

// Sanitizes the given text if needed by replacing bad words with '*'.
exports.sanitizeText = (text) => {
  // Re-capitalize if the user is Shouting.
  if (isShouting(text)) {
    console.log('User is shouting. Fixing sentence case...');
    text = stopShouting(text);
  }

  // Moderate if the user uses SwearWords.
  if (containsSwearwords(text)) {
    console.log('User is swearing. moderating...');
    text = replaceSwearwords(text);
  }

  return text;
};

// Returns true if the string contains swearwords.
function containsSwearwords(message) {
  return message !== badWordsFilter.clean(message);
}

// Hide all swearwords. e.g: Crap => ****.
function replaceSwearwords(message) {
  return badWordsFilter.clean(message);
}

// Detect if the current message is shouting. i.e. there are too many Uppercase
// characters or exclamation points.
function isShouting(message) {
  return message.replace(/[^A-Z]/g, '').length > message.length / 2 || message.replace(/[^!]/g, '').length >= 3;
}
