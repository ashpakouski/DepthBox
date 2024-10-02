# DepthBox 🌌

**DepthBox** is a composable component that utilizes the power of Android's AGSL shaders to create a depth effect by placing its child composables at a selected depth level of a specified background image.

At the moment, it's just a prototype and doesn't support depth map generation, so you need to obtain one yourself or use a Google Camera photo taken in Portrait mode (it has a depth map embedded in the image).

* `GoogleCameraDepthImageRepository` is used to retrieve the required image components from a single Google Camera photo.
* `DefaultDoubleSourceImageRepository` allows you to provide your own depth map. Make sure the depth map image has the same aspect ratio as the main image.

## Examples

|![1](https://github.com/user-attachments/assets/a450d33f-a81c-46cc-8631-668628b1d8f7)|![2](https://github.com/user-attachments/assets/a605c63c-9212-4a21-be32-88b3144c2f65)|![3](https://github.com/user-attachments/assets/cf3133cf-f48c-476e-a3a3-918acaf786d1)|
| :---: | :---: | :---: |
|![4](https://github.com/user-attachments/assets/7a9f67c8-5286-4054-8307-d18e45307ece)|![5](https://github.com/user-attachments/assets/c7878854-352f-4c44-96a7-cb4e3a1f0819)|![6](https://github.com/user-attachments/assets/f201b031-e58c-4461-91df-9a8c95e96c33)|

## Follow for more

[<img src="https://github.com/AndreiShpakovskiy/BmpViewer/assets/50966785/026046f6-a25d-49bf-861d-776629be14aa" height="50">](https://linkedin.com/in/ashpakouski)
