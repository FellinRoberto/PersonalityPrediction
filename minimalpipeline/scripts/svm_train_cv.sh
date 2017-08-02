svmlighttk="SVM-Light-1.5-rer"
pwd=$("pwd")
echo "pwd: $pwd"

bash_source="${BASH_SOURCE[0]}"
echo "bash_source: $bash_source"

DIR="$( cd $( dirname ${BASH_SOURCE[0]} ) && pwd )"
echo "DIR: $DIR"

cd "$DIR/.."



echo "this.pwd: $(pwd)"

src=${1:?"missing."}
dest=${2:?"missing."}
params=${3:?"missing."}

#params="-t 5 -F 3 -C +"

echo "src: $src"
echo "dest: $dest"


if [ -z "$src" ]; then
    echo "src not specified"
    exit 1
fi
if [ ! -d "$src" ]; then
    echo "directory \"$src\" does not exist"
    exit 1
fi
echo "srcdir: $src"


if [ -z "$dest" ]; then
    echo "dest not specified"
    exit 1
fi
mkdir -p "$dest"
echo "destdir: $dest"

if [ -z "$params" ]; then
    echo "params not specified"
    exit 1
fi

for filename in "$src"/*; do
    if [ -d "$filename" ]; then
        #echo "\"$filename\" is a directory."
        n_fold=${filename##*/}
        
        models="$dest/$n_fold"
        #echo "models: \"$models\""

	mkdir -p "$models"
        
        examples=$filename
        
        for file in "$examples"/*.train; do
            if [ -f "$file" ]; then
                example_file=$file
                #echo "\"$example_file\" is an example file."
                
                modelname="${example_file##*/}"
                modelname="${modelname%.*}"
                model_file="$models/${modelname}.model"
                
                #echo "\"$model_file\" is dest model file."
                
                #echo "$svmlighttk/svm_learn $params $example_file $model_file"
		eval "$svmlighttk/svm_learn  $params $example_file $model_file "
		#$( "svmlighttk/svm_learn $params $example_file $model_file" )
                #svmlighttk_fullcmd="$svmlighttk/svm_learn $params $example_file $model_file"
                #$($svmlighttk_fullcmd)                
            fi
        done        
        
    else
        echo "\"$filename\" is a file."
    fi
done


cd $pwd
