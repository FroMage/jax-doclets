/*
    Copyright 2009 Lunatech Research

    This file is part of jax-doclets.

    jax-doclets is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jax-doclets is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with jax-doclets.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lunatech.doclets.jax.jpa.writers;

import java.io.IOException;
import java.util.Collection;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jpa.model.JPAClass;
import com.lunatech.doclets.jax.jpa.model.JPAMember;
import com.lunatech.doclets.jax.jpa.model.MemberType;
import com.lunatech.doclets.jax.jpa.model.Relation;
import com.sun.javadoc.Doc;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class JPAClassWriter extends DocletWriter {

	public JPAClassWriter(JAXConfiguration configuration, JPAClass jpaClass) {
		super(configuration, getWriter(configuration, jpaClass), jpaClass);
	}

	private static HtmlDocletWriter getWriter(JAXConfiguration configuration, JPAClass jpaClass) {
		try {
			return new HtmlDocletWriter(configuration.parentConfiguration, Utils.classToPath(jpaClass), jpaClass.getShortClassName() + ".html",
					Utils.classToRoot(jpaClass));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void write() {
		printHeader();
		printMenu("");
		printSummary();
		printColumns();
		tag("hr");
		printMenu("");
		printFooter();
		writer.flush();
		writer.close();
	}

	private void printColumns() {
		printMembers(jpaClass.getMembers(), "IDs", MemberType.ID);
		printMembers(jpaClass.getColumns(), "Columns", MemberType.Column);
		printMembers(jpaClass.getRelations(), "Relations", MemberType.Relation);
	}

	private void printMembers(Collection<? extends JPAMember> members, String title, MemberType type) {
		if (members.isEmpty())
			return;
		tag("hr");
		open("table class='info' id='" + title + "'");
		boolean isRelation = type == MemberType.Relation;
		boolean isID = type == MemberType.ID;
		around("caption class='TableCaption'", title);
		open("tbody");
		open("tr");
		around("th class='TableHeader'", "Name");
		around("th class='TableHeader'", "Type");
		if(isRelation)
			around("th class='TableHeader'", "Relation");
		around("th class='TableHeader'", "Description");
		close("tr");
		for (JPAMember member : members) {
			if(isID && !member.isID())
				continue;
			open("tr");
			if(!isID){
				open("td id='m_" + member.getName() + "'");
				print(member.getName());
			}else{
				open("td");
				around("a href='#m_"+member.getName()+"'", member.getName());
			}
			close("td");
			open("td");
			printMemberType(member, true);
			if(!isID && member.isID()){
				around("b", "[ID]");
			}
			if(member.getSequence() != null){
				tag("br");
				print("sequence: "+member.getSequence());
			}
			close("td");
			if(isRelation){
				open("td");
				Relation rel = (Relation) member;
				print(rel.getRelationFrom().name());
				print("..");
				print(rel.getRelationTo().name());
				close("td");
			}
			open("td");
			Doc javaDoc = member.getJavaDoc();
			if (javaDoc != null && javaDoc.firstSentenceTags() != null)
				writer.printSummaryComment(javaDoc);
			close("td");
			close("tr");

		}
		close("tbody");
		close("table");
	}

	private void printMemberType(JPAMember member, boolean markCollections) {
		if (markCollections && member.isCollection())
			print("[");
		if (member.isJPAType()) {
			String name = member.getJavaTypeName();
			JPAClass typeClass = jpaClass.getRegistry().getJPAClass(name);
			around("a href='" + Utils.urlToClass(jpaClass, typeClass) + "'", typeClass.getName());
		} else
			print(member.getSQLType());
		if (markCollections && member.isCollection())
			print("]");
	}

	private void printSummary() {
		open("h2 class='classname'");
		around("span class='name'", "Name: " + jpaClass.getName());
		close("h2");
		Doc javaDoc = jpaClass.getJavaDoc();
		if (javaDoc != null && javaDoc.tags() != null) {
			writer.printInlineComment(javaDoc);
		}
		open("dl");
		JPAMember idMember = jpaClass.getID();
		if (idMember != null) {
			open("dt");
			around("b", "ID");
			close("dt");
			open("dd");
			around("a href='#m_" + idMember.getName() + "'", idMember.getName());
			close("dd");
		}
		close("dl");
	}

	protected void printHeader() {
		printHeader("Table " + jpaClass.getName());
	}

	@Override
	protected void printTopMenu(String selected) {
		open("table", "tbody", "tr");
		printMenuItem("Overview", writer.relativePath + "index.html", selected);
		printOtherMenuItems(selected);
		close("tr", "tbody", "table");
	}

	protected void printThirdMenu() {
		open("tr");
		open("td class='NavBarCell3' colspan='2'");
		print("detail: ");
		printLink(true, "#IDs", "id");
		print(" ");
		printLink(!jpaClass.getColumns().isEmpty(), "#Columns", "column");
		print(" ");
		printLink(!jpaClass.getRelations().isEmpty(), "#Relations", "relation");
		close("td", "tr");
	}
	
  protected void printOtherMenuItems(String selected) {
    printMenuItem("Graph", writer.relativePath + "graph.html", selected);
  }

}
