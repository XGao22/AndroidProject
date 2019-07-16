package com.tjkcht.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * Created by APPLE on 2019/2/18.
 */

public class MaterialXmlParseUtils {

    static   String MaterialJSON;
    public static String  MaterialJSON( InputStream in) throws  Exception{
        XmlPullParser parser= Xml.newPullParser();
        parser.setInput(in,"utf-8");
        int type=parser.getEventType();


        while(type!=XmlPullParser.END_DOCUMENT){

            switch (type){
                case XmlPullParser.START_TAG:
                    if("string".equals(parser.getName())){
                        MaterialJSON=parser.nextText();
                    }
                    break;
            }
            type=parser.next();
        }


        return MaterialJSON;
    }
}
