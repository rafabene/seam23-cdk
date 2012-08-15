package org.jboss.seam.workaround.seamcdk;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal execute
 * 
 * @phase generate-sources
 */
public class WorkaroundGenereatorMojo extends AbstractMojo
{
   /**
    * The source directories containing the sources to be compiled.
    * 
    * @parameter expression="${project.build.sourceDirectory}"
    * @required
    * @readonly
    */
   protected String sourceDirectory;

   /**
    * Output directory for processed resources
    * 
    * @parameter expression="${project.build.directory}"
    * @required
    */
   private String targetDirectory;

   private ConverterGenerator conveterGenerator;
   private ValidatorGenerator validatorGenerator;

   public void execute() throws MojoExecutionException
   {
      conveterGenerator = new ConverterGenerator(sourceDirectory, targetDirectory, getLog());
      validatorGenerator = new ValidatorGenerator(targetDirectory, getLog());
      try
      {
         File sourceFolder = new File(sourceDirectory);
         getLog().info("Source Folder: " + sourceFolder);
         visitFolder(sourceFolder);
         conveterGenerator.generateConverters();
         visitFolder(new File(sourceFolder.getParent(), "config/component"));
         validatorGenerator.generateValidators();
      }
      catch (Exception e)
      {
         throw new MojoExecutionException("Error on Generator", e);
      }

   }

   private void visitFolder(File sourceFolder) throws FileNotFoundException
   {
      for (File file : sourceFolder.listFiles())
      {
         if (file.isDirectory())
         {
            visitFolder(file);
         }
         else
         {
            conveterGenerator.addFile(file);
            validatorGenerator.addFile(file);
         }
      }
   }
}
