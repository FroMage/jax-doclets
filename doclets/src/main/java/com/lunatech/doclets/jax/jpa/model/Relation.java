package com.lunatech.doclets.jax.jpa.model;

import javax.persistence.JoinTable;

import com.lunatech.doclets.jax.Utils;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

public class Relation extends JPAMember {

	private RelationSize relationFrom;
	private RelationSize relationTo;
	private boolean owning;
	private AnnotationDesc joinTable;
	private String mappedBy;

	public Relation(JPAClass jpaClass, ProgramElementDoc property, String name,
			AnnotationDesc columnAnnotation, AnnotationDesc relation) {
		super(jpaClass, property, name, columnAnnotation);
		String relationName = relation.annotationType().name();
	    if(relationName.equals("OneToMany") || relationName.equals("OneToOne"))
	    	relationFrom = RelationSize.ONE;
	    else
	    	relationFrom = RelationSize.MANY;
	    if(relationName.equals("ManyToOne") || relationName.equals("OneToOne"))
	    	relationTo = RelationSize.ONE;
	    else
	    	relationTo = RelationSize.MANY;
	    
	    mappedBy = (String) Utils.getAnnotationValue(relation, "mappedBy");
	    
	    if(isManyToMany()){
	    	joinTable = Utils.findAnnotation(property, JoinTable.class);
	    	if(joinTable != null){
	    		owning = true;
	    	}else if(mappedBy == null)
				throw new RuntimeException("Missing mappedBy for non-owning many-to-many: "+klass.getName()+"."+name);
	    }else if(columnAnnotation != null)
	    	owning = mappedBy != null;
	    else
	    	owning = true;
	}

	private boolean isManyToMany() {
		return relationFrom == RelationSize.MANY
		&& relationTo == RelationSize.MANY;
	}

	public RelationSize getRelationFrom() {
		return relationFrom;
	}

	public RelationSize getRelationTo() {
		return relationTo;
	}
	
	@Override
	public String getName() {
		if(!isManyToMany())
			return super.getName();
		String table, property;
		Type target = getJavaType();
		JPAClass targetJPA = klass.getRegistry().getJPAClass(target.qualifiedTypeName());
		if(!owning){
			Relation inverseRelation = targetJPA.getRelation(mappedBy);
			joinTable = inverseRelation.joinTable;
		}
		if(joinTable == null){
			table = klass.getName() + "_" + target.typeName();
			property = target.typeName();
		}else{
			table = (String) Utils.getAnnotationValue(joinTable, "name");
			AnnotationValue[] joinColumns = (AnnotationValue[]) Utils.getAnnotationValue(joinTable, owning ? "joinColumns" : "inverseJoinColumns");
			AnnotationDesc joinColumn = (AnnotationDesc) joinColumns[0].value();
			property = (String) Utils.getAnnotationValue(joinColumn, "name");
		}
		return table + "." + property;
	}
}
