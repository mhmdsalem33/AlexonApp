package com.mhmd.alexon.domain.models

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val brand: String,
    val category: String,
    val description: String,
    val discountPercentage: Double,
    val id: Int,
    val images: List<String>,
    val price: Int,
    val rating: Double,
    val stock: Int,
    val thumbnail: String,
    val title: String
) : Parcelable {

    override fun describeContents(): Int {
        return 0
    }
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(brand)
        dest.writeString(category)
        dest.writeString(description)
        dest.writeDouble(discountPercentage)
        dest.writeInt(id)
        dest.writeStringList(images)
        dest.writeInt(price)
        dest.writeDouble(rating)
        dest.writeInt(stock)
        dest.writeString(thumbnail)
        dest.writeString(title)
    }
    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(source: Parcel): Product {
            return Product(
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readDouble(),
                source.readInt(),
                source.createStringArrayList()!!,
                source.readInt(),
                source.readDouble(),
                source.readInt(),
                source.readString()!!,
                source.readString()!!
            )
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }



}
