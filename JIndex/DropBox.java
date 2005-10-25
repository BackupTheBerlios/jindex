import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;

import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.FileSeekableStream;

public class DropBox {

	 public static void main(String[] args) {
         /* Validate input. */
         if (args.length != 1) {
             System.out.println("Usage: java JAISampleProgram " +
                                "input_image_filename");
             System.exit(-1);
         }

         /*
          * Create an input stream from the specified file name
          * to be used with the file decoding operator.
          */
         FileSeekableStream stream = null;
         try {
             stream = new FileSeekableStream(args[0]);
         } catch (IOException e) {
             e.printStackTrace();
             System.exit(0);
         }

         /* Create an operator to decode the image file. */
         RenderedOp image1 = JAI.create("stream", stream);
         /*
          * Create a standard bilinear interpolation object to be
          * used with the "scale" operator.
          */
         Interpolation interp = Interpolation.getInstance(
                                    Interpolation.INTERP_BILINEAR);

         /**
          * Stores the required input source and parameters in a
          * ParameterBlock to be sent to the operation registry,
          * and eventually to the "scale" operator.
          */
         ParameterBlock params = new ParameterBlock();
         params.addSource(image1);
         params.add(1.0F);         // x scale factor
         params.add(1.0F);         // y scale factor
         params.add(0.0F);         // x translate
         params.add(0.0F);         // y translate
         params.add(interp);       // interpolation method
         /* Create an operator to scale image1. */
         RenderedOp image2 = JAI.create("scale", params);

         /* Get the width and height of image2. */
         int width = image2.getWidth();
         int height = image2.getHeight();
         
         image2.getAsBufferedImage();

    System.out.println(width+"\t"+height);     

         
     }
}
