package org.sam.jspacewars.serialization;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshaller;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy;
import com.thoughtworks.xstream.core.ReferenceByXPathUnmarshaller;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.core.TreeUnmarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

@SuppressWarnings("deprecation")
final class ReusingReferenceByXPathMarshallingStrategy implements MarshallingStrategy {
	
	private TreeMarshaller marshaller;
	private TreeUnmarshaller unmarshaller;
	
	private int mode;
	
	public ReusingReferenceByXPathMarshallingStrategy() {
        this(ReferenceByXPathMarshallingStrategy.RELATIVE);
    }

    public ReusingReferenceByXPathMarshallingStrategy(int mode) {
        this.mode = mode;
    }
	
    public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, ConverterLookup converterLookup, Mapper mapper) {

    	if(unmarshaller == null)
    		unmarshaller = new ReferenceByXPathUnmarshaller(root, reader, converterLookup, mapper){
    			// TODO Mirar en futuras vesiones para quitar esta ñapa que adapta las referencias
	    		protected Object getReferenceKey(String reference) {
//	    			System.out.println( reference );
	    			if(reference.startsWith("/Instancia3D[")){
	    				String number = reference.substring("/Instancia3D[".length(),reference.indexOf(']'));
	    				String resto = reference.substring(reference.indexOf("]/")+2);
	    				StringBuffer newReference = new StringBuffer("/NaveUsuario/");
	    				newReference.append(resto.substring(0,resto.indexOf('/')));
	    				newReference.append('[');
	    				newReference.append(number);
	    				newReference.append(']');
	    				newReference.append(resto.substring(resto.indexOf('/')));
	    				reference = newReference.toString();
	    			}else if(reference.startsWith("/Instancia3D")){
	    				StringBuffer newReference = new StringBuffer("/NaveUsuario");
	    				newReference.append(reference.substring("/Instancia3D".length()));
	    				reference = newReference.toString();
	    			}
	    			return super.getReferenceKey( reference );
	    		}
	    	};
    	return unmarshaller.start(dataHolder);
    }

    public void marshal(HierarchicalStreamWriter writer, Object obj, ConverterLookup converterLookup, Mapper mapper, DataHolder dataHolder) {
    	if( marshaller == null )
    		marshaller = new ReferenceByXPathMarshaller(writer, converterLookup, mapper, mode);
    	marshaller.start(obj, dataHolder);
    }
    
    /**
     * @deprecated As of 1.2, use {@link #unmarshal(Object, HierarchicalStreamReader, DataHolder, ConverterLookup, Mapper)}
     */
    public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, DefaultConverterLookup converterLookup, ClassMapper classMapper) {
        return unmarshal(root, reader, dataHolder, (ConverterLookup)converterLookup, (Mapper)classMapper);
    }

    /**
     * @deprecated As of 1.2, use {@link #marshal(HierarchicalStreamWriter, Object, ConverterLookup, Mapper, DataHolder)}
     */
    public void marshal(HierarchicalStreamWriter writer, Object obj, DefaultConverterLookup converterLookup, ClassMapper classMapper, DataHolder dataHolder) {
        marshal(writer, obj, converterLookup, (Mapper)classMapper, dataHolder);
    }
}
