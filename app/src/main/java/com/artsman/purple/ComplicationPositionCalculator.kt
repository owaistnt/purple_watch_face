package com.artsman.purple

import android.graphics.Rect

class ComplicationPositionCalculator {
    var width: Int = 0;
    var sizeOfComplication: Int=0
    var midpointOfScreen: Int=0

    var horizontalOffset =0
    var verticalOffset = 0

    private constructor(width : Int){
        this.width=width
        sizeOfComplication= width / 4
        midpointOfScreen= width / 2

        horizontalOffset = (midpointOfScreen - sizeOfComplication) / 2
        verticalOffset = midpointOfScreen - sizeOfComplication / 2


    }

    fun getLeftRect()=Rect(
        horizontalOffset,
        verticalOffset,
        horizontalOffset + sizeOfComplication,
        verticalOffset + sizeOfComplication
    )


    fun getRightRect()=Rect(
        (midpointOfScreen + horizontalOffset),
        verticalOffset,
        (midpointOfScreen + horizontalOffset + sizeOfComplication),
        (verticalOffset + sizeOfComplication))

    companion object{
        fun getNewInstance(width: Int): ComplicationPositionCalculator{
            return ComplicationPositionCalculator(width)
        }
    }
}