package com.example.imagelabeling

import com.example.imagelabeling.databinding.ActivityMainBinding


import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val REQUEST_IMAGE_CAPTURE=1

    private var imageBitmap:Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.apply {

            captureImage.setOnClickListener {

                takeImage()

                textView.text=""

            }

            detectImage.setOnClickListener {

                if (imageBitmap!=null) {

                    processImage()
                }
                else{

                    Toast.makeText(this@MainActivity, "Select a photo first", Toast.LENGTH_SHORT).show()

                }


            }



        }



    }

    private fun processImage() {

        val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromBitmap(imageBitmap!!, 0)
        imageLabeler.process(inputImage).addOnSuccessListener { imageLabels ->
            var stringImageRecognition = ""
            for (imageLabel in imageLabels) {
                val stingLabel = imageLabel.text
                val floatConfidence = imageLabel.confidence
                val index=imageLabel.index
                stringImageRecognition += "$index\n $stingLabel:\n $floatConfidence "
            }
            binding.textView.text = stringImageRecognition
        }


    }

    private fun takeImage() {

        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        intent.putExtra("crop", "true")
        intent.putExtra("scale", true)
        intent.putExtra("return-data", true)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK){

            val extras: Bundle? = data?.extras
            if (extras != null) {
                //Get image
                imageBitmap = extras.getParcelable("data")
            }

            if (imageBitmap!=null){

                binding.imageView.setImageBitmap(imageBitmap)


            }


        }



    }





}