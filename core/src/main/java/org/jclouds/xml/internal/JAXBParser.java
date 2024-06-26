/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.xml.internal;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.jclouds.Constants;
import org.jclouds.xml.XMLParser;

import com.google.inject.name.Named;

/**
 * Parses XML documents using JAXB.
 * 
 * @see ParseXMLWithJAXB
 */
@Singleton
public class JAXBParser implements XMLParser {

   /** Boolean indicating if the output must be pretty printed. */
   private Boolean prettyPrint;

   @Inject
   public JAXBParser(@Named(Constants.PROPERTY_PRETTY_PRINT_PAYLOADS) String prettyPrint) {
      super();
      this.prettyPrint = Boolean.valueOf(prettyPrint);
   }

   @Override
   public String toXML(final Object src) throws IOException {
      return toXML(src, src.getClass());
   }

   @Override
   public <T> String toXML(final Object src, final Class<T> type) throws IOException {
      try {
         JAXBContext context = JAXBContext.newInstance(type);
         Marshaller marshaller = context.createMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);
         StringWriter writer = new StringWriter();
         marshaller.marshal(src, writer);
         return writer.toString();
      } catch (JAXBException ex) {
         throw new IOException("Could not marshall object", ex);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T fromXML(String xml, final Class<T> type) throws IOException {
      // ignore byte order mark
      if (xml.charAt(0) == 0xFEFF) {
         xml = xml.substring(1);
      }
      try {
         StringReader reader = new StringReader(xml);
         JAXBContext context = JAXBContext.newInstance(type);
         Unmarshaller unmarshaller = context.createUnmarshaller();
         return (T) unmarshaller.unmarshal(reader);
      } catch (Exception ex) {
         throw new IOException("Could not unmarshal document into type: " + type.getSimpleName() + "\n" + xml, ex);
      }
   }
}
