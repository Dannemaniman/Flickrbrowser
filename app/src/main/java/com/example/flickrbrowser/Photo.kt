package com.example.flickrbrowser

import android.os.Parcel
import android.os.Parcelable

//Obs! Många klagar på att Serializable är långsamt, och Parcelable är att föredra, men det beror lite på vad du vill göra.
//Men imed att detta är en väldigt enkel class, så fungerar det utmärkt.
//The problem with performance arises, because the JVM has been given no indication of how to serialize our class, so it has to use something
//called reflection to work it all out.
//Reflection involves examining the class at runtime, to work out the fields that need to be stored in the byte stream
//We can do a lot better than that by implementing three additional functions!

class Photo(var title: String, var author: String, var authorId: String, var link: String, var tags: String,
            var image: String) : Parcelable {


    //För enklaste sättet att integrera Parcelable, skriv : Parcelable, sen håll musen över Photo och välj implement parcelable eller vad det står
    //Ifall lightbulben inte kommer upp, ta bort : Parcelable och hovera musen över Photo i class declarationen och så ska det komma.
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(authorId)
        parcel.writeString(link)
        parcel.writeString(tags)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }

    //Were not going to be storing the image itself in the object. We dont want to be storing any more data then
    //we have to on our mobile device,  and thats because storage is limited on a mobile device. So were going to
    //use a much smarter process, to only download the image just before we need it.
    override fun toString(): String{
        return "Photo(title='$title', author='$author', authorId='$authorId', link='$link', tags='$tags', image='$image')"
    }

    //The version number's used by the serialization code, to check that the data its retrieving, is the same version as the data that was stored.
    //If I dont create one, then Java will create one for me. Different versions create different UIDs, and thats why you get problems by
    //not defining your own.










//OBS!!! Serializable finns nu för Kotlin =)

    //Lägg till : Serializable på class declaration och även import java.io.Serializable
//
//    companion object {
//        private const val serialVersionUID = 1L
//    }

    //Om jag förstår det rätt kan JVM lösa detta vid runtime via Reflection, men om jag gör det själv här så går det snabbare pga
    // JVM inte behöver tänka över vad som ska hamna vart vid serializing och de-serializing
    //Serializa objectet!
    //Annotations are means of attaching metadata to code. To declare an annotation,
    // put the annotation modifier in front of a class, @Throws i detta fallet
//    @Throws(IOException::class)
//    private fun writeObject(out: java.io.ObjectOutputStream) {
//        Log.d("Photo", "writeObject called")
//        out.writeUTF(title)
//        out.writeUTF(author)
//        out.writeUTF(authorId)
//        out.writeUTF(link)
//        out.writeUTF(tags)
//        out.writeUTF(image)
//    }
//    //Complementary function, "to take data off the stream and set the values into our class properties
//    @Throws(IOException::class, ClassNotFoundException::class)
//    private fun readObject(inStream: java.io.ObjectInputStream) {
//        Log.d("Photo", "readObject called")
//        title = inStream.readUTF()
//        author = inStream.readUTF()
//        authorId = inStream.readUTF()
//        link = inStream.readUTF()
//        tags = inStream.readUTF()
//        image = inStream.readUTF()
//    }
//
//    //Dont worry too much about this function, its intended for rare cases when you've created a subclass of the class that was originally
//    //serialized.
//    @Throws(ObjectStreamException::class)
//    private fun readObjectNoData() {
//        Log.d("Photo", "readObjectNoData called")
//    }

}