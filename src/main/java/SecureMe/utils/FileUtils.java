package SecureMe.utils;

import java.io.File;
import java.util.Random;

public class FileUtils {

    public static Boolean filesEqual(byte[] a,byte[] b)
    {
        if(a.length!=b.length)
            return false;
        else{

            Random random = new Random();
            int start= random.nextInt((int) Math.round(a.length*0.75));
            int end=start+random.nextInt(b.length-start);
            for(int i=0;i<end;i++)
            {
                if(a[i]!=b[i])
                    return false;
            }



            return true;
        }


    }




}
