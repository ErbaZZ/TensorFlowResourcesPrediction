## Android Phone Resources Usage Pattern Prediction Using TensorFlow

  This project is trying to evaluate the feasibility of using TensorFlow to analyze mobile
phone status records data to predict the future usage of the phone resources per user basis,
which could be helpful in optimizing the background services resources usage, especially for
WiFi and Bluetooth modules. The mobile phone usage behavior varies for each users, and the
resources being used are not the same, so in order to predict that, TensorFlow is used to
create a machine learning model and deploy on mobile phones to make inferences on the
future resources usage statuses.

In this project, Keras, a high-level neural network API with TensorFlow backend is used
on Python to create a machine learning model. The model is then trained with an Android
phone status records dataset. After that, the trained model is loaded into an Android
application for inferencing. The application would then gathers the status data of the Android
phone and feed into the Inference Interface with the model to get the prediction result.
