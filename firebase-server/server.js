const express = require('express');
const admin = require('firebase-admin');
const bodyParser = require('body-parser');
const cors = require('cors');

require('dotenv').config();

// Initialize Firebase Admin SDK using environment variables
admin.initializeApp({
    credential: admin.credential.cert({
        projectId: process.env.FIREBASE_PROJECT_ID,
        privateKey: process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/g, '\n'), // Handle escaped newlines
        clientEmail: process.env.FIREBASE_CLIENT_EMAIL,
    }),
});

const app = express();
app.use(bodyParser.json());
app.use(cors());

app.get('/', (req, res) => {
    res.send('Welcome to Firebase Notification Server!');
});

// Endpoint to send a notification
app.post('/sendNotification', (req, res) => {
    const { token, eventID, title, body } = req.body;

    const message = {
        data: {
            eventID: eventID,
        },

        notification: {
            title: title || "Notification",
            body: body || "No body provided",
        },

        token: token,
    };

    admin.messaging().send(message)
        .then((response) => {
            console.log('Successfully sent message:', response);
            res.status(200).send({ success: true, response });
        })
        .catch((error) => {
            console.error('Error sending message:', error);
            res.status(500).send({ success: false, error });
        });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
