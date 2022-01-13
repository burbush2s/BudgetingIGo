/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

// [START all]
// [START import]
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');
// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();
// [END import]
const db = admin.firestore();

// [START addMessage]
// Get the Budget Models already defined
/*exports.getBugetingModels = functions.https.onRequest(async (req, res) => {
  console.log('Starting query BugetingModels');
  const modelsRef = db.collection('BudgetingModels');
  const snapshot = await modelsRef.where('enabled', '==', true).get();
  if (snapshot.empty) {
    console.log('No matching documents.');
    return;
  }  
  let budgetingModels=[];
  snapshot.forEach(doc => {
    console.log("snapshot data: "+JSON.stringify(doc.data()));
    let concepts = doc.data().concepts;
    const conceptsMap = Object.entries(concepts);

    console.log("map:" + conceptsMap.size);  
    let jsonObject = {
      "name":doc.data().name,
      "enabled":doc.data().enabled,
      "concepts":conceptsMap
    };  
    budgetingModels.push(jsonObject);
  });
  res.json(budgetingModels);
  // [END adminSdkAdd]
});*/
// [END addMessage]

exports.getBudgetingModelsAndroid = functions.https.onCall((data, context) => {
  console.log('Starting query BugetingModels');
  let budgetingModels=[];
  const modelsRef = db.collection('BudgetingModels');
  const snapshot =  modelsRef.where('enabled', '==', true).get().then(querySnapshot => {
    querySnapshot.forEach(documentSnapshot => {
      console.log("document: ", documentSnapshot);
      console.log("snapshot data: "+JSON.stringify(documentSnapshot.data()));
      let concepts = documentSnapshot.data().concepts;
      const conceptsMap = Object.entries(concepts);

      console.log("map:" + conceptsMap.size);  
      let jsonObject = {
        "name":documentSnapshot.data().name,
        "enabled":documentSnapshot.data().enabled,
        "concepts":conceptsMap
      };  
      budgetingModels.push(jsonObject);
    });
    console.log("budgetingModels ",budgetingModels);
    return budgetingModels;
  }).catch((error) => {
    // Re-throwing the error as an HttpsError so that the client gets the error details.
      throw new functions.https.HttpsError('unknown', error.message, error);
  });

  
  // snapshot.forEach(doc => {
    // console.log("snapshot data: "+JSON.stringify(doc.data()));
    // let concepts = doc.data().concepts;
    // const conceptsMap = Object.entries(concepts);

    // console.log("map:" + conceptsMap.size);  
    // let jsonObject = {
    //   "name":doc.data().name,
    //   "enabled":doc.data().enabled,
    //   "concepts":conceptsMap
    // };  
    // budgetingModels.push(jsonObject);
    // console.log(doc.id, '=>', doc.data());
  // });
  
  
  // [END adminSdkAdd]
});

/*exports.saveSelectedModel = functions.https.onCall((data, context) => {
  console.log('Starting query saveSelectedModel');
  //Get selected model and initial quantity
  const selectedModel = JSON.parse(data.query.model);
  console.log('selectedModel '+JSON.stringify(selectedModel));
  const initialQuantity = Number(data.query.initialQuantity);
  console.log('initialQuantity '+initialQuantity);
  if (selectedModel != null){
    if(initialQuantity>0){
      if(selectedModel.concepts != null){
        let conceptsMap = selectedModel.concepts;
        var result = new Map();
        var spent = new Map();
        console.log('concepts '+JSON.stringify(conceptsMap));
        for (const key of conceptsMap.values())  {
          console.log(key);
          console.log("conceptsMap[key] "+key[0]+ " "+key[1]);
          result.set(key[0], (key[1]*initialQuantity)/100);
          spent.set(key[0], 0);
        }
        const resultMap = Object.fromEntries(result);
        const resultSpent = Object.fromEntries(spent);
        console.log("result: "+JSON.stringify(resultMap));
        const res1 = await db.collection('generalBalance').doc(context.auth.uid).set({"selectedModel":selectedModel, "budget":resultMap, "spent":resultSpent});
      }
    }
  } else{
    console.log('No model received.');
  }
  
  return res1;
  // [END adminSdkAdd]
});*/

exports.addMessage = functions.https.onCall((data, context) => {
  // [START_EXCLUDE]
  // [START readMessageData]
  // Message text passed from the client.
  const text = data.text;
  // [END readMessageData]
  // [START messageHttpsErrors]
  // Checking attribute.
  if (!(typeof text === 'string') || text.length === 0) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
        'one arguments "text" containing the message text to add.');
  }
  // Checking that the user is authenticated.
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
  // [END messageHttpsErrors]

  // [START authIntegration]
  // Authentication / user information is automatically added to the request.
  const uid = context.auth.uid;
  const name = context.auth.token.name || null;
  const picture = context.auth.token.picture || null;
  const email = context.auth.token.email || null;
  // [END authIntegration]

  // [START returnMessageAsync]
  // Saving the new message to the Realtime Database.
  const sanitizedMessage = text; // Sanitize the message.
  return admin.database().ref('/messages').push({
    text: sanitizedMessage,
    author: { uid, name, picture, email },
  }).then(() => {
    console.log('New Message written');
    // Returning the sanitized message to the client.
    return { text: sanitizedMessage };
  })
  // [END returnMessageAsync]
    .catch((error) => {
    // Re-throwing the error as an HttpsError so that the client gets the error details.
      throw new functions.https.HttpsError('unknown', error.message, error);
    });
  // [END_EXCLUDE]
});
