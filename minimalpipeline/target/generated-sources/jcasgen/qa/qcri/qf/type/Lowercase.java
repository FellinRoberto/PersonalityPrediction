

   
/* Apache UIMA v3 - First created by JCasGen Wed Aug 02 10:37:43 CEST 2017 */

package qa.qcri.qf.type;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;


import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Aug 02 10:37:43 CEST 2017
 * XML source: /home/roberto/DISCO/UNIVERSITA/MAGISTRALE/ANL/PersonalityPrediction/minimalpipeline/target/jcasgen/typesystem.xml
 * @generated */
public class Lowercase extends Annotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "qa.qcri.qf.type.Lowercase";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Lowercase.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
 
  /* *******************
   *   Feature Offsets *
   * *******************/ 
   
  public final static String _FeatName_uppercaseVersion = "uppercaseVersion";
  public final static String _FeatName_postag = "postag";


  /* Feature Adjusted Offsets */
  public final static int _FI_uppercaseVersion = TypeSystemImpl.getAdjustedFeatureOffset("uppercaseVersion");
  public final static int _FI_postag = TypeSystemImpl.getAdjustedFeatureOffset("postag");

   
  /** Never called.  Disable default constructor
   * @generated */
  protected Lowercase() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public Lowercase(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Lowercase(JCas jcas) {
    super(jcas);
    readObject();   
  } 


  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Lowercase(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: uppercaseVersion

  /** getter for uppercaseVersion - gets 
   * @generated
   * @return value of the feature 
   */
  public String getUppercaseVersion() { return _getStringValueNc(_FI_uppercaseVersion);}
    
  /** setter for uppercaseVersion - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUppercaseVersion(String v) {
    _setStringValueNfc(_FI_uppercaseVersion, v);
  }    
    
   
    
  //*--------------*
  //* Feature: postag

  /** getter for postag - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPostag() { return _getStringValueNc(_FI_postag);}
    
  /** setter for postag - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPostag(String v) {
    _setStringValueNfc(_FI_postag, v);
  }    
    
  }

    