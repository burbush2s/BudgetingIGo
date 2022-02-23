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
 const { getStorage } = require('firebase-admin/storage');
 // The Firebase Admin SDK to access the Firebase Realtime Database.
 const admin = require('firebase-admin');
 const xl = require('excel4node');
 
 
 admin.initializeApp({
   storageBucket: 'budgetingigo.appspot.com'
 });
 
 const path = require('path');
 const os = require('os');
 
 // [END import]
 const db = admin.firestore();
 const bucket = getStorage().bucket();
 const auth = admin.auth();
 
 exports.scheduledFunction = functions.pubsub.schedule('every 50 minutes').onRun(async(context) => {
   //Getting users
   const listAllUsers = (nextPageToken) => {
     // List batch of users, 1000 at a time.
     auth
       .listUsers(1000, nextPageToken)
       .then((listUsersResult) => {
         listUsersResult.users.forEach((userRecord) => {
           console.log('user: ', userRecord.uid+ " email :"+userRecord.email);
           console.log('Starting query Movements');
 
           const modelsRef = db.collection('movements');
           const snapshot =  modelsRef.where("user","==", userRecord.uid)
           //.where("date",'<=',dateEnd).where("date",'>=',dateStart)
           .orderBy("date", 'desc')
           .get().then(querySnapshot => {
             console.log('after querying');
             if(!querySnapshot.empty){
               try {
                 createFile(userRecord, querySnapshot);
               }
               catch(error) {
                   console.log(error, "error when creating file");
               }
             }
           }).catch((error) => {
             // Re-throwing the error as an HttpsError so that the client gets the error details.
               throw new functions.https.HttpsError('ERROR AFTER GETTING FILES', error.message, error);
           });
         });
         if (listUsersResult.pageToken) {
           // List next batch of users.
           listAllUsers(listUsersResult.pageToken);
         }
       })
       .catch((error) => {
         console.log('Error listing users:', error);
       });
   };
   // Start listing users from the beginning, 1000 at a time.
   listAllUsers();
 
   console.log('This will be run every 3 minutes!');
   await delay(1000);

   return null;
 });
 
 async function createFile(user, querySnapshot){
   // ...
   let dateEnd = new Date()
   let dateStart = new Date()
   dateStart.setDate(dateEnd.getDate()-1)
   var counter = 2;
   var userRecord = user;
   var querySnapshot = querySnapshot;
   console.log('There are documents');
                 //Starting creating the file
               // Create a new instance of a Workbook class
               var workbook = new xl.Workbook();
 
               // Add Worksheets to the workbook
               var worksheet = workbook.addWorksheet('Movements');
 
               // Create a reusable style
               const style = workbook.createStyle({
                 font: {
                   color: '#FF0800',
                   size: 12
                 },
                 numberFormat: '$#,##0.00; ($#,##0.00); -'
               });
               // Set value of cell A1 to 100 as a number type styled with paramaters of style
               worksheet.cell(1, 1).string("Concept").style(style);
 
               // Set value of cell B1 to 300 as a number type styled with paramaters of style
               worksheet.cell(1, 2).string("Amount").style(style);
               worksheet.cell(1, 3).string("Description").style(style);
               worksheet.cell(1, 4).string("Date").style(style);
 
               querySnapshot.forEach(documentSnapshot => {
                 console.log("document: ", documentSnapshot);
                 console.log("snapshot data: "+JSON.stringify(documentSnapshot.data()));
                 worksheet.cell(counter, 1).string(documentSnapshot.data().concept);
                 worksheet.cell(counter, 2).number(documentSnapshot.data().amount);
                 worksheet.cell(counter, 3).string(documentSnapshot.data().description);
 
                 var date = documentSnapshot.data().date.toDate().toDateString();
                 worksheet.cell(counter, 4).date(date);
                 counter++;
               });
 
               console.log('Documents were processed');
               let month = dateEnd.getUTCMonth()+1;
               const fileName = dateEnd.getFullYear()+"_"+month+"_"+dateEnd.getUTCDate()+"_"+dateEnd.getTime()+".xlsx";
               //const fileName = dateEnd.getFullYear()+"_"+month+"_"+dateEnd.getUTCDate()+"_"+dateEnd.getTime()+".xlsx";
               var tempFilePath = path.join(os.tmpdir(), fileName);
               var folder = "user/"+userRecord.uid;
 
               var options = {
                 destination: folder+"/"+fileName,
                 resumable: true,
                 validation: 'crc32c',
                 metadata: {
                   metadata: {
                     event: 'Fall trip to the zoo'
                   }
                 }
               };
 
               functions.logger.log('destination: ', folder+"/"+fileName);
               await workbook.write(tempFilePath)
               await delay(500);
               console.log('Excel file created');
               functions.logger.log('Before writing to Cloud Storage', new Date());
               await bucket.upload(tempFilePath, options)
               
               return null; 
               
               //Saving file in Cloud Storage
               //Writing to Cloud Storage
 
 }
 

 function delay(time) {
    return new Promise(res => {
      setTimeout(() => {
        res("VALUE TO RESOLVE");
      }, time);
    });
  }
 
 exports.sendEmail = functions.storage.object().onFinalize(async (object) => {
   // ...
   let filePath = object.name;
   console.log("filePath: ",filePath)
   let filePathArray = filePath.split("/");
   let dateArray = filePathArray[2].split("_")
   console.log("filePathArray[1]: ",filePathArray[1])
   auth
   .getUser(filePathArray[1])
   .then((userRecord) => {
     // See the UserRecord reference doc for the contents of userRecord.
     console.log("Successfully fetched user data:"+userRecord);
     console.log("URL: "+"https://storage.googleapis.com/budgetingigo.appspot.com/"+filePath);
     admin
    .firestore()
    .collection("mail")
    .add({
        to: userRecord.email,
        message: {
        subject: "Your monthly report! Budgeting I go.",
        text: "Here is your movements' report of the month number" +dateArray[1]+".",
        html: "Here is your movements' report of the month number" +dateArray[1]+".",
        attachments: [
                {
                    fileName:filePathArray[2],
                    path: "https://storage.googleapis.com/budgetingigo.appspot.com/"+filePath
                }
            ]
        },
    })
  .then(() => console.log("Queued email for delivery!"));
   })
   .catch((error) => {
     console.log('Error fetching user data:', error);
   });
 });