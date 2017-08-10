java -cp target/dependency/*:target/qa.qcri.qf.pipeline-0.0.1-SNAPSHOT.jar \
 -Djava.util.logging.config.file="resources/logging.properties" \
 -Xss128m \
 qa.qcri.qf.pipeline.trec.TrecPipelineRunner \
 -argumentsFilePath arguments/trec-en-pipeline-arguments.txt
