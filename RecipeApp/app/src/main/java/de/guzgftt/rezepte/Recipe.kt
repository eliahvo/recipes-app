package de.guzgftt.rezepte

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Recipe  constructor(var name : String?, var picture : String?) : Serializable{
    /*constructor(parcel: Parcel) : this(
        parcel.readString(),
        //parcel.readParcelable(Bitmap::class.java.classLoader)
        //parcel.readParcelable(Uri::class.java.classLoader)
    )

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0!!.writeString(name)
        //p0!!.writeParcelable(bm, p1)
        p0!!.writeString(picture)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }*/


}