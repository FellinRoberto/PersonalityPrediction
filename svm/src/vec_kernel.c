
# include "svm_common.h"
//short  CONFIG_VECT; // if 1 the vector of parameters is defined

PAR_VEC pv[MAX_NUM_VECTOR_PARM];
TKP tree_kernel_params[MAX_NUMBER_OF_TREES];

//local functions
int isDefined(PAR_VEC * );
void setConfiguration(PAR_VEC * , KERNEL_PARM * );
void getConfiguration(KERNEL_PARM * , PAR_VEC * );
void copyPAR_VEC(PAR_VEC * , PAR_VEC * );
int isDefinedTREE(TKP * );
void setConfigurationTREE(TKP * , KERNEL_PARM * );
void getConfigurationTREE(KERNEL_PARM * , TKP * );
void copyPAR_TREE(TKP * , TKP * );
double multi_vector_kernel(KERNEL_PARM *, DOC *, DOC * );
double _multi_vector_kernel_not_norm(KERNEL_PARM *, DOC *, DOC *, int );

/*
	typedef struct param_vec{
  long t;					kernel_type
  long d;					poly_degree
  double g;				rbf_gamma
  double s;				coef_lin
  double r;				coef_const
  char u[64];			custom
  double L;				lambda
  double M;				mu
  long N;					normalization
  double weight;	weight
	} PAR_VEC;
	*/

//a->supvec; b->doc
double multi_vector_kernel(KERNEL_PARM *kernel_parm, DOC *a, DOC *b)
{

	int arr_dim = a->num_of_vectors + a->num_of_trees;
	//printf("Il numero di tree e': %d\nIl numero di treeEvec e': %d\n", a->num_of_trees, arr_dim);fflush(stdout);
	double k[arr_dim];
	double weight[arr_dim];
	//double last_weight=0;
	//double tot_weight=0;
	PAR_VEC k_parm_last_conf;
	PAR_VEC k_parm_first_conf;
	TKP k_parm_last_conf_tree;
	TKP k_parm_first_conf_tree;
	int isFirstVector = 1;
	int isFirstTree = 1;
	int i, j, z;
	double k_tot=0;

	//imposta l'ultima configurazione con i parametri di default
	k_parm_last_conf.t = 0;
	k_parm_last_conf.d = 3;
	k_parm_last_conf.g = 1.0;
	k_parm_last_conf.s = 1;
	k_parm_last_conf.r = 1;
	strcpy(k_parm_last_conf.u, "empty");
	k_parm_last_conf.L = 0.4;
	k_parm_last_conf.M = 0.4;
	k_parm_last_conf.N = 3;
	k_parm_last_conf.weight = 1;


	k_parm_last_conf_tree.kernel_type = 6;
	k_parm_last_conf_tree.tree_kernel_type = 3;
	k_parm_last_conf_tree.lambda = 0.4;
	k_parm_last_conf_tree.mu = 0.4;
	k_parm_last_conf_tree.normalization = 3;
	k_parm_last_conf_tree.TKGENERALITY = 3;
	k_parm_last_conf_tree.weight = 1;

	k_parm_first_conf_tree.kernel_type = 6;
	k_parm_first_conf_tree.tree_kernel_type = 3;
	k_parm_first_conf_tree.lambda = 0.4;
	k_parm_first_conf_tree.mu = 0.4;
	k_parm_first_conf_tree.normalization = 3;
	k_parm_first_conf_tree.TKGENERALITY = 3;
	k_parm_first_conf_tree.weight = 1;


	for(i=0;i<b->num_of_vectors;i++)
	{
		//cerca nell'array pv se sono definiti dei parametri specifici per il vettore
		if(PARAM_VECT == 1)//ci sta un file di parametri
		{

			if(isFirstVector)
			{
				//salvo la configurazione iniziale
				setConfiguration(&k_parm_first_conf, kernel_parm);
				isFirstVector = 0;
			}

			//se e' definito il vettore i-esimo nel file dei parametri (basta che almeno una componente NON sia 0)
			if(isDefined(&(pv[i])))
			{
				//imposta i parametri di kernel_parm come sono specificati nel file di parametri secondo quel vettore
				getConfiguration(kernel_parm, &(pv[i]));

				weight[i] = pv[i].weight;

				//imposta l'ultima configurazione
				copyPAR_VEC(&(pv[i]), &k_parm_last_conf);

			}
			else//il vettore non ha una sua configurazione legge l'ultima configurazione
			{
				//TEST
				//printf("Il vettore %d NON ha una sua configurazione!\n", i);fflush(stdout);
				getConfiguration(kernel_parm, &k_parm_last_conf);

				weight[i] = k_parm_last_conf.weight;

			}

		}

		if(kernel_parm->combination_type == 'T' || kernel_parm->combination_type == 'V')
			kernel_parm->combination_type = '+';//valore di default per il multi_vector

		//TEST
		//printf("Configuration_type: %c\n", kernel_parm->combination_type);
		//printf("a->num_vectors: %d  b->num_vectors: %d\n", a->num_of_vectors, b->num_of_vectors);fflush(stdout);


		//TODO E' NECESSARIO UN kernel_type maggiore di 4?
		//TODO qua non arrivano alberi
		if(kernel_parm->normalization == 3)
		{
			if(i< a->num_of_vectors && i< b->num_of_vectors)
				if(a->vectors[i]!=NULL && b->vectors[i]!=NULL){

					//CODICE CAMBIATO, prima era: k[i] = _multi_vector_kernel_not_norm(kernel_parm, a, b, i)/sqrt(_multi_vector_kernel_not_norm(kernel_parm, a, a, i) * _multi_vector_kernel_not_norm(kernel_parm, b, b, i));
					double ret_ker=_multi_vector_kernel_not_norm(kernel_parm, a, b, i);
					k[i] = ret_ker/sqrt(a->vectors[i]->twonorm_STD * b->vectors[i]->twonorm_STD);
					if(verbosity>=4){
						printf("vector kernel k[%d] not normalized:%lf norm1=%lf, norm2=%lf\n", i, ret_ker, a->vectors[i]->twonorm_STD, b->vectors[i]->twonorm_STD);
						printf("vector kernel k[%d]=%lf\n", i, k[i]);
					}
					//printf("NUM:%lf\n", _multi_vector_kernel_not_norm(kernel_parm, a, b, i));
					//printf("NORMA 1 NEW:%lf CACHE:%lf\n", _multi_vector_kernel_not_norm(kernel_parm, a, a, i), a->vectors[i]->twonorm_STD);
					//printf("versione fast: %lf\n",_multi_vector_kernel_not_norm(kernel_parm, a, b, i)/sqrt(a->vectors[i]->twonorm_STD * b->vectors[i]->twonorm_STD));
				}

		}
		else
			if(i< a->num_of_vectors && i< b->num_of_vectors)
				if(a->vectors[i]!=NULL && b->vectors[i]!=NULL){

					k[i] = _multi_vector_kernel_not_norm(kernel_parm, a, b, i);
					if(verbosity>=4){
						printf("vector kernel k[%d]=%lf\n", i, k[i]);
					}
				}

	}
	if(i>0){//AGGIUNTO
		//ripristino i valori
		getConfiguration(kernel_parm, &k_parm_first_conf);
	}

	for(z=0;z<b->num_of_trees;z++)
	{
		//printf("\nPasso 0\n");
		//printf("\n\nEntro nella sezione dei tree\n\n");
		//fflush(stdout);
		//cerca nell'array pv se sono definiti dei parametri specifici per il vettore
		if(PARAM_VECT == 1)//ci sta un file di parametri
		{
			if(isFirstTree)
			{
				//salvo la configurazione iniziale
				//printf("STO PER CHIAMARE setConfigurationTREE\n");
				//fflush(stdout);
				setConfigurationTREE(&k_parm_first_conf_tree, kernel_parm);
				isFirstTree = 0;
				//printf("HO CHIAMATO setConfigurationTREE\n");
				//fflush(stdout);
			}

			//se e' definito il vettore i-esimo nel file dei parametri (basta che almeno una componente NON sia 0)
			if(isDefinedTREE(&(tree_kernel_params[z])))
			{
				//imposta i parametri di kernel_parm come sono specificati nel file di parametri secondo quel vettore

				getConfigurationTREE(kernel_parm, &(tree_kernel_params[z]));


				weight[i+z] = tree_kernel_params[z].weight;

				//imposta l'ultima configurazione
				copyPAR_TREE(&(tree_kernel_params[z]), &k_parm_last_conf_tree);

			}
			else//il vettore non ha una sua configurazione legge l'ultima configurazione
			{
				//TEST
				//printf("Il vettore %d NON ha una sua configurazione!\n", i);fflush(stdout);
				getConfigurationTREE(kernel_parm, &k_parm_last_conf_tree);

				weight[i+z] = k_parm_last_conf_tree.weight;

			}

		}

		if(kernel_parm->combination_type == 'T' || kernel_parm->combination_type == 'V')
			kernel_parm->combination_type = '+';//valore di default per il multi_vector

		//TEST
		//printf("Configuration_type: %c\n", kernel_parm->combination_type);
		//printf("a->num_vectors: %d  b->num_vectors: %d\n", a->num_of_vectors, b->num_of_vectors);fflush(stdout);


		if(kernel_parm->normalization == 3)
		{
			fflush(stdout);
			if(z< a->num_of_trees && z< b->num_of_trees)
				if(a->forest_vec[z]!=NULL && b->forest_vec[z]!=NULL){
					//printf("QUI\n");
					double ret_ker=_multi_vector_kernel_not_norm(kernel_parm, a, b, z);

					//CODICE CAMBIATO, prima era: k[i+z] = _multi_vector_kernel_not_norm(kernel_parm, a, b, z)/sqrt(_multi_vector_kernel_not_norm(kernel_parm, a, a, z) * _multi_vector_kernel_not_norm(kernel_parm, b, b, z));
					k[i+z] = ret_ker/sqrt(a->forest_vec[z]->twonorm_PT * b->forest_vec[z]->twonorm_PT);
					if(verbosity>=4){
						printf("tree kernel k[%d]=%lf norma1=%lf norma2=%lf\n", z, ret_ker, a->forest_vec[z]->twonorm_PT, b->forest_vec[z]->twonorm_PT);
						printf("tree kernel k[%d]=%lf\n", z, k[i+z]);
						fflush(stdout);
					}
				}

		}
		else
			if(z< a->num_of_trees && z< b->num_of_trees)
				if(a->forest_vec[z]!=NULL && b->forest_vec[z]!=NULL){
					k[i+z] = _multi_vector_kernel_not_norm(kernel_parm, a, b, z);
					if(verbosity>=4){
						printf("tree kernel k[%d]=%lf\n", z, k[i+z]);
					}
				}

	}



	//printf("Stampo il numero z: %ld\n", arr_dim);


//	getConfigurationTREE(kernel_parm, &k_parm_first_conf_tree);

	//ripristino i valori
	if(z>0){//AGGIUNTO
		getConfigurationTREE(kernel_parm, &k_parm_first_conf_tree);
	}

	//TODO non posso vederlo, spostarlo al momento di lettura del file dei parametri
	/*for(j=0;j<i+z;j++)
		tot_weight += weight[j];
	*/
	switch(kernel_parm->combination_type)
	{
		case '+':

			for(j=0;j<i+z;j++){
				//double t=weight[j]*k[j];
				//printf("Summing:\t%f * %f=%f\n", weight[j], k[j], t);
				k_tot += (double)weight[j]*k[j];
			}
			//printf("\n");

			break;

		case '*':

			k_tot = 1;
			for(j=0;j<i+z;j++)
				k_tot *= (double)weight[j]*k[j];

			break;

		default: printf("Error: Unknown kernel combination!\n"); exit(1);
	}

	if(verbosity>=4){
		printf("multi kernel k_tot:%lf\n", k_tot);
	}

	//printf("%f\n", k_tot);
	return k_tot;

}


double _multi_vector_kernel_not_norm(KERNEL_PARM *kernel_parm, DOC *a, DOC *b, int num_vec)
{
	//TODO posso invocare kernel in svm_common.c? MA FORSE ANCHE NO
	int i, j;
	i = num_vec;
	j = num_vec;
	//printf("IN MULTI VECTOR...\n");
	//fflush(stdout);
	//printf("i=%d\n",i);
	//fflush(stdout);
	if((kernel_parm->kernel_type==6 && a->forest_vec[i]!=NULL && b->forest_vec[i]!=NULL) || (a->vectors[i]!=NULL && b->vectors[i]!=NULL)) // Check if the i-th vectors are empty.
	{
		//printf("NELL'IF\n");
		//printf("in _multi_kernel_not_norm con kernel_parm->kernel_type=%ld kernel_parm->poly_degree=%ld, kernel_parm->rbf_gamma=%lf, vectors[%d]->twonorm_sq=%lf\n", kernel_parm->kernel_type, kernel_parm->poly_degree, kernel_parm->rbf_gamma, i,a->vectors[i]->twonorm_sq);
		fflush(stdout);
		//printf("in _multi_kernel_not_norm con kernel_parm->kernel_type=%ld\n", kernel_parm->kernel_type);
		//fflush(stdout);
		switch(kernel_parm->kernel_type) {
    	case 0: // linear
            return(sprod_i(a, b, i, j));
        case 1: // polynomial
            return (double) pow(((double)kernel_parm->coef_lin)*(double)sprod_i(a, b, i, j)
                   +(double)kernel_parm->coef_const,(double) kernel_parm->poly_degree);
        case 2: // radial basis function
            return(exp(-kernel_parm->rbf_gamma*(a->vectors[i]->twonorm_sq-2*sprod_i(a, b, i, j)+b->vectors[i]->twonorm_sq)));
        	//return(exp(-kernel_parm->rbf_gamma*(a->vectors[i]->twonorm_sq-2*sprod_ss(a->vectors[i]->words,b->vectors[j]->words)+b->vectors[i]->twonorm_sq)));
        case 3: // sigmoid neural net
            return(tanh(kernel_parm->coef_lin*sprod_i(a, b, i, j)+kernel_parm->coef_const));
    	case 4: // custom-kernel supplied in file kernel.h
            return(custom_kernel(kernel_parm,a,b));
        case 5: /* combine kernels */
                return ((CFLOAT)advanced_kernels(kernel_parm,a,b));
        case 6: /* tree kernel*/
                //return tree_kernel(kernel_parm, a, b, i, j);
        	return tree_kernel_not_norm(kernel_parm, a, b, i, j);
        //case 7:
				//return tree_kernel_not_norm(kernel_parm, a, b, i, j);

    		default: printf("Error: The kernel function is unknown\n"); exit(1);

  		}
	}

	return 0;

}

//funzione che verifica se e' definito nel file dei parametri la configurazione per il vettore i-esimo
//(basta che almeno una componente NON sia 0)
int isDefined(PAR_VEC *pv)
{
	if(pv->t != 0)
		return 1;
	if(pv->d != 0)
		return 1;
	if(pv->g != 0)
		return 1;
	if(pv->s != 0)
		return 1;
	if(pv->r != 0)
		return 1;
	if(pv->L != 0)
		return 1;
	if(pv->M != 0)
		return 1;
	if(pv->N != 0)
		return 1;
	if(pv->weight != 0)
		return 1;

	return 0;
}

int isDefinedTREE(TKP *tree_kp)
{
	if(tree_kp->tree_kernel_type != 0)
		return 1;
	if(tree_kp->kernel_type != 0)
		return 1;
	if(tree_kp->TKGENERALITY != 0)
		return 1;
	if(tree_kp->lambda != 0)
		return 1;
	if(tree_kp->mu != 0)
		return 1;
	if(tree_kp->normalization != 0)
		return 1;
	if(tree_kp->weight != 0)
		return 1;

	return 0;
}

void setConfiguration(PAR_VEC *k_parm_first_conf, KERNEL_PARM *kernel_parm)
{
	k_parm_first_conf->t = kernel_parm->kernel_type;
	k_parm_first_conf->d = kernel_parm->poly_degree;
	k_parm_first_conf->g = kernel_parm->rbf_gamma;
	k_parm_first_conf->s = kernel_parm->coef_lin;
	k_parm_first_conf->r = kernel_parm->coef_const;
	strcpy(k_parm_first_conf->u, kernel_parm->custom);
	k_parm_first_conf->L = kernel_parm->lambda;
	k_parm_first_conf->M = kernel_parm->mu;
	k_parm_first_conf->N = kernel_parm->normalization;
	//weight non si salva*/

}

void setConfigurationTREE(TKP *k_parm_first_conf, KERNEL_PARM *kernel_parm)
{
	k_parm_first_conf->kernel_type = kernel_parm->kernel_type;
	k_parm_first_conf->tree_kernel_type = kernel_parm->first_kernel;
	k_parm_first_conf->TKGENERALITY = 3;
	k_parm_first_conf->lambda = kernel_parm->lambda;
	k_parm_first_conf->mu = kernel_parm->mu;
	k_parm_first_conf->normalization = kernel_parm->normalization;
	//weight non si salva*/

}

void getConfiguration(KERNEL_PARM *kernel_parm, PAR_VEC *pv)
{
	kernel_parm->kernel_type = pv->t;
	kernel_parm->poly_degree = pv->d;
	kernel_parm->rbf_gamma = pv->g;
	kernel_parm->coef_lin = pv->s;
	kernel_parm->coef_const = pv->r;
	strcpy(kernel_parm->custom, pv->u);
	kernel_parm->lambda = pv->L;
	kernel_parm->mu = pv->M;
	kernel_parm->normalization = pv->N;

}

void getConfigurationTREE(KERNEL_PARM *kernel_parm, TKP *tree_kernel_params)
{
	kernel_parm->kernel_type = tree_kernel_params->kernel_type;
	kernel_parm->first_kernel = tree_kernel_params->tree_kernel_type;
	//TODEL
	//printf("AAAAAAAAAAAAAAAAAAAAAAA %ld\n\n", kernel_parm->first_kernel);
	//exit(0);

	LAMBDA = tree_kernel_params->lambda;
	LAMBDA2 = LAMBDA*LAMBDA;
	MU=tree_kernel_params->mu;
	TKGENERALITY=tree_kernel_params->TKGENERALITY;

}


void copyPAR_VEC(PAR_VEC *pv, PAR_VEC *k_parm_last_conf)
{
	k_parm_last_conf->t = pv->t;
	k_parm_last_conf->d = pv->d;
	k_parm_last_conf->g = pv->g;
	k_parm_last_conf->s = pv->s;
	k_parm_last_conf->r = pv->r;
	strcpy(k_parm_last_conf->u, pv->u);
	k_parm_last_conf->L = pv->L;
	k_parm_last_conf->M = pv->M;
	k_parm_last_conf->N = pv->N;
	k_parm_last_conf->weight = pv->weight;

}

void copyPAR_TREE(TKP *tree_kp, TKP *k_parm_last_conf_tree)
{
	k_parm_last_conf_tree->kernel_type = tree_kp->kernel_type;
	k_parm_last_conf_tree->tree_kernel_type = tree_kp->tree_kernel_type;
	k_parm_last_conf_tree->TKGENERALITY = tree_kp->TKGENERALITY;
	k_parm_last_conf_tree->lambda = tree_kp->lambda;
	k_parm_last_conf_tree->mu = tree_kp->mu;
	k_parm_last_conf_tree->normalization = tree_kp->normalization;
	k_parm_last_conf_tree->weight = tree_kp->weight;

}
