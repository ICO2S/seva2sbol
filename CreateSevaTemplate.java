
import org.sbolstandard.core2.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class CreateSevaTemplate
{
    public static void main(String[] args) throws Exception
    {
        SBOLDocument doc = new SBOLDocument();
        doc.setDefaultURIprefix("http://synbiohub.org/public/seva/");

        ComponentDefinition pSevaTemplate = doc.createComponentDefinition("pSevaTemplate", "1", ComponentDefinition.DNA);

        String na = readFile("data/pSevaTemplate.txt", StandardCharsets.US_ASCII);
        Sequence pSevaTemplateSeq = doc.createSequence("pSevaTemplateSeq", "1", na, Sequence.IUPAC_DNA);
        pSevaTemplate.addSequence(pSevaTemplateSeq);

        ComponentDefinition T0 = annotate(na, doc, pSevaTemplate, "T0", 15, 117, OrientationType.INLINE);
        ComponentDefinition oriT = annotate(na, doc, pSevaTemplate, "oriT", 154, 399, OrientationType.INLINE);

        ComponentDefinition PS1 = annotate(na, doc, pSevaTemplate, "PS1", 425, 442, OrientationType.REVERSECOMPLEMENT);
        PS1.addRole(SequenceOntology.PRIMER_BINDING_SITE);

        ComponentDefinition PS2 = annotate(na, doc, pSevaTemplate, "PS2", 73, 89, OrientationType.REVERSECOMPLEMENT);
        PS2.addRole(SequenceOntology.PRIMER_BINDING_SITE);

        ComponentDefinition PS3 = annotate(na, doc, pSevaTemplate, "PS3", 73, 89, OrientationType.INLINE);
        PS3.addRole(SequenceOntology.PRIMER_BINDING_SITE);

        ComponentDefinition PS4 = annotate(na, doc, pSevaTemplate, "PS4", 377, 394, OrientationType.INLINE);
        PS4.addRole(SequenceOntology.PRIMER_BINDING_SITE);

        ComponentDefinition PS5 = annotate(na, doc, pSevaTemplate, "PS5", 170, 187, OrientationType.INLINE);
        PS5.addRole(SequenceOntology.PRIMER_BINDING_SITE);

        ComponentDefinition PS6 = annotate(na, doc, pSevaTemplate, "PS6", 425, 442, OrientationType.REVERSECOMPLEMENT);
        PS6.addRole(SequenceOntology.PRIMER_BINDING_SITE);




        ComponentDefinition PacI = annotate(na, doc, pSevaTemplate, "PacI", 1, 8, OrientationType.INLINE);
        PacI.addRole(SequenceOntology.RESTRICTION_ENZYME_RECOGNITION_SITE);

        ComponentDefinition SpeI = annotate(na, doc, pSevaTemplate, "SpeI", 9, 14, OrientationType.INLINE);
        SpeI.addRole(SequenceOntology.RESTRICTION_ENZYME_RECOGNITION_SITE);

        ComponentDefinition cargo_deletion = annotate(na, doc, pSevaTemplate, "cargo_deletion", 9, 14, OrientationType.INLINE);
        cargo_deletion.addRole(SequenceOntology.ENGINEERED_REGION);

        ComponentDefinition cargo = annotate(na, doc, pSevaTemplate, "cargo", 8, OrientationType.INLINE);
        cargo.addRole(SequenceOntology.ENGINEERED_REGION);



        ComponentDefinition FseI = annotate(na, doc, pSevaTemplate, "FseI", 400, 407, OrientationType.INLINE);
        FseI.addRole(SequenceOntology.RESTRICTION_ENZYME_RECOGNITION_SITE);

        ComponentDefinition AscI = annotate(na, doc, pSevaTemplate, "AscI", 408, 415, OrientationType.INLINE);
        AscI.addRole(SequenceOntology.RESTRICTION_ENZYME_RECOGNITION_SITE);

        ComponentDefinition oriR_deletion = annotate(na, doc, pSevaTemplate, "oriR_deletion", 404, 409, OrientationType.INLINE);
        oriR_deletion.addRole(SequenceOntology.ENGINEERED_REGION);

        ComponentDefinition oriR = annotate(na, doc, pSevaTemplate, "oriR", 527, OrientationType.INLINE);
        oriR.addRole(SequenceOntology.ENGINEERED_REGION);



        ComponentDefinition SwaI = annotate(na, doc, pSevaTemplate, "SwaI", 136, 143, OrientationType.INLINE);
        SwaI.addRole(SequenceOntology.RESTRICTION_ENZYME_RECOGNITION_SITE);

        ComponentDefinition PshAI = annotate(na, doc, pSevaTemplate, "PshAI", 144, 153, OrientationType.INLINE);
        PshAI.addRole(SequenceOntology.RESTRICTION_ENZYME_RECOGNITION_SITE);

        ComponentDefinition antibiotic_resistance_deletion = annotate(na, doc, pSevaTemplate, "antibiotic_resistance_deletion", 140, 148, OrientationType.INLINE);
        antibiotic_resistance_deletion.addRole(SequenceOntology.ENGINEERED_REGION);

        ComponentDefinition antibiotic_resistance = annotate(na, doc, pSevaTemplate, "antibiotic_resistance", 143, OrientationType.INLINE);
        antibiotic_resistance.addRole(SequenceOntology.ENGINEERED_REGION);





        doc.write("out/pSevaTemplate.xml");
    }

    public static ComponentDefinition annotate(String na, SBOLDocument doc, ComponentDefinition context, String name, int start, int end, OrientationType orientation) throws SBOLValidationException
    {
        ComponentDefinition cd = doc.createComponentDefinition(name, "1", ComponentDefinition.DNA);

        String subseq = na.substring(start - 1, end);

        if(orientation == OrientationType.REVERSECOMPLEMENT)
        {
            subseq = reverseComplement(subseq);
        }

        Sequence seq = doc.createSequence(name + "_seq", "1", subseq, Sequence.IUPAC_DNA);
        cd.addSequence(seq);

        context.createComponent(name, AccessType.PUBLIC, cd.getIdentity());

        context.createSequenceAnnotation(name + "_annotation", name + "_range", start, end, orientation);

        return cd;
    }

    public static ComponentDefinition annotate(String na, SBOLDocument doc, ComponentDefinition context, String name, int cut, OrientationType orientation) throws SBOLValidationException
    {
        ComponentDefinition cd = doc.createComponentDefinition(name, "1", ComponentDefinition.DNA);
        context.createComponent(name, AccessType.PUBLIC, cd.getIdentity());

        SequenceAnnotation sa = context.createSequenceAnnotation(name + "_annotation", name + "_cut", cut, orientation);

        return cd;
    }


    public static String reverseComplement(String na)
    {
        String out = "";

        for(int i = na.length() - 1; i >= 0; -- i)
        {
            if(na.charAt(i) == 'a')
                out = out + "t";
            else if(na.charAt(i) == 't')
                out = out + "a";
            else if(na.charAt(i) == 'c')
                out = out + "g";
            else if(na.charAt(i) == 'g')
                out = out + "c";
        }

        return out;
    }

    // http://stackoverflow.com/a/326440
    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}

