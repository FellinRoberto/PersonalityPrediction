svmlighttk="SVM-Light-1.5-rer"
pwd=$("pwd")
echo "pwd: $pwd"

bash_source="${BASH_SOURCE[0]}"
echo "bash_source: $bash_source"

DIR="$( cd $( dirname ${BASH_SOURCE[0]} ) && pwd )"
echo "DIR: $DIR"

cd "$DIR/.."
echo "this.path: $(pwd)" 

echo "pwd: $pwd"

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

for file in "$src"/*.train; do
    if [ -f "$file" ]; then
        echo "file: $file"
	example_file=$file
	echo "example_file: $example_file"
	filename_and_ext="${file##*/}"
	filename="${filename_and_ext%.*}"
	echo "filename: $filename"
	model_file="$dest/${filename}.model"
	echo "model_file: $model_file"
	eval "$svmlighttk/svm_learn  $params $example_file $model_file "
    fi
done

cd $pwd

