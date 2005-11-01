import java.io.File;

import org.stringfellow.jFileWatch.FileWatch;
import org.stringfellow.jFileWatch.FileWatchEvent;
import org.stringfellow.jFileWatch.FileWatchEventListener;
import org.stringfellow.jFileWatch.FileWatchFactory;

/* Demo.java -- Shows examples of AWT components
   Copyright (C) 1998, 1999, 2002, 2004 Free Software Foundation, Inc.

This file is part of GNU Classpath examples.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA. */



class Demo
{
  public static void main(String args[])
  {
	  FileWatch fw = FileWatchFactory.create();
      fw.addWatch(new File("/tmp"));
      fw.addListener(new FileWatchEventListener()
      {
          public void processFileWatchEvent(FileWatchEvent evt)
          {
              System.err.println(evt);
          }
      });
  }
}
