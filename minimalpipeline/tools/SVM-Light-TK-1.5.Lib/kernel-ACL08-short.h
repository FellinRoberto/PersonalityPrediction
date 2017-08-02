/************************************************************************/
/*                                                                      */
/*   kernel.h                                                           */
/*                                                                      */
/*   User defined kernel function. Feel free to plug in your own.       */
/*                                                                      */
/*   Copyright: Alessandro Moschitti                                    */
/*   Date: 20.11.06                                                     */
/*                                                                      */
/************************************************************************/

/* KERNEL_PARM is defined in svm_common.h The field 'custom' is reserved for */
/* parameters of the user defined kernel. */
/* Here is an example of custom kernel on a forest and vectors*/                          

/*
struct tree_kernel_parameters{
			short kernel_type;
			short TKGENERALITY;
			double lambda;
			double mu;
			double weight;
			short normalization;
}

#define ACL07_KERNEL		-1	// Moschtti et al. 2007
#define SUBTREE_KERNEL		0  // VISHTANAM ans SMOLA 2002
#define SUBSET_TREE_KERNEL	1  // COLLINS and DUFFY 2002
#define BOW_SST_KERNEL		2 // ZHANG, 2003
#define PARTIAL_TREE_KERNEL	3 // Moschitti, ECML 2006
#define NOLEAVES_PT_KERNEL	4 //
#define STRING_KERNEL		6 // Taylor and Cristianini book 2004
#define NOKERNEL           -10 // don't use this tree in any tree kernel

*/

TKP tree_kernel_params[MAX_NUMBER_OF_TREES];/*={
//									QUESTION

//   PT  			  BOW				POS
{ACL07_KERNEL,1,.4,.4,1,1}, {ACL07_KERNEL,1,.4,.4,1,1}, {NOKERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},	// 0-4

// PTs
{NOKERNEL,1,.4,1,1,1},		{NOKERNEL,1,.4,1,1,1},		{NOKERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},		// 5-9

// PAS
{NOKERNEL,1,.4,1,1,1},		{NOKERNEL,1,.4,1,1,1},		{NOKERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},		// 10-14

//									ANSWER

//    PT			 BOW            POS			  
{ACL07_KERNEL,1,.4,1,1,1}, {ACL07_KERNEL,1,.4,1,1,1},	{NOKERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},			// 15-19

//			PAS0					  PAS1							PAS2					PAS3			PAS4
{ACL07_KERNEL,1,.4,1,1,1}, {ACL07_KERNEL,1,.4,1,1,1},	{ACL07_KERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},{NOKERNEL,1,.4,1,1,1},				// 20-24

// END

{END_OF_TREE_KERNELS,0,0,0,0,0}

};
*/

double custom_kernel(KERNEL_PARM *kernel_parm, DOC *a, DOC *b) 
{

  int i,j;
  double k =0;
  
  for(i=0; i< 20; i++){
   if (tree_kernel_params[i].kernel_type!=NOKERNEL){
			if(tree_kernel_params[i].kernel_type==END_OF_TREE_KERNELS){
				printf("\nERROR: the parameter found in the parameter file/VECTOR are lower than");
				printf("\n       those required by the trees in the forest (only %d trees are fulfilled)\n\n",i);
				exit(-1);
			}

		kernel_parm->first_kernel=tree_kernel_params[i].kernel_type;
		LAMBDA = tree_kernel_params[i].lambda; 
		LAMBDA2 = LAMBDA*LAMBDA;
		MU=tree_kernel_params[i].mu;
		TKGENERALITY=tree_kernel_params[i].TKGENERALITY;

        k += tree_kernel_params[i].weight*tree_kernel(kernel_parm, a, b, i, i); 
		//printf("kernel %f\n",k);
    }
  }

  i=20;kernel_parm->first_kernel=tree_kernel_params[i].kernel_type;
  LAMBDA = tree_kernel_params[i].lambda; 
  LAMBDA2 = LAMBDA*LAMBDA;
  MU=tree_kernel_params[i].mu;
  TKGENERALITY=tree_kernel_params[i].TKGENERALITY;
  
  for(i=20; i<23 && i < a->num_of_trees; i++)
	 for(j=20; j<23 && j< b->num_of_trees; j++)
		 if (tree_kernel_params[i].kernel_type!=NOKERNEL||tree_kernel_params[j].kernel_type!=NOKERNEL)
			k += tree_kernel(kernel_parm, a,b,i,j)*kernel_parm->tree_constant;

  
  return k;
}
/*
{-1,1,.4,.4,1,1},  {-1,1,.4,.4,1,1},  {-10,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},	// 0-4

// PTs
{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},		// 5-9

// PAS
{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},		// 10-14

//									ANSWER

//    PT			 BOW            POS			  
{-1,1,.4,1,1,1},{-1,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},			// 15-19

//    PAS0			PAS1			PAS2		  PAS3			PAS4
{1,1,.4,1,1,1},{1,1,.4,1,1,1},{1,1,.4,1,1,1},{-10,1,.4,1,1,1},{-10,1,.4,1,1,1},				// 20-24

// END

{END_OF_TREE_KERNELS,0,0,0,0,0}

};*/