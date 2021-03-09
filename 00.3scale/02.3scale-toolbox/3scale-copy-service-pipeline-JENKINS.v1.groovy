pipeline {
	agent any
	stages {
		stage('Setting parameters') {
			steps {
				script{
					env.ACCESS_TOKEN_DEV = "b81727d39397d3a8fca71bc64f25aee68fa29a816ac71cf32ba7641498430866" //dev_toolbox_token
					env.ACCESS_TOKEN_TEST = "2e2191d61cbfa1bed4d502678c8b0a6977df29110920f659fc86b4ff3083a7a0" //test_toolbox_token
					env.ACCESS_TOKEN_PROD = "4b25111c4b0e413235f4a4bc04ce1e4778ab866c9cf3e54010c4209a83d6dc72"  //prod_toolbox_token
					
					env.URL_DEV = "dev-3scale-admin.apps.osepext01.claro.amx"
					env.URL_TEST = "test-3scale-admin.apps.osepext01.claro.amx"
					env.URL_PROD = "prod-3scale-admin.apps.osepext01.claro.amx"
				}
			}
		}
		stage("Select Tenant") {
			steps {
				script{
					env.TENANT_SOURCES = ""
					env.TENANT_DESTINATION = ""
					
					def INPUT_PARAMS = input message: 'Seleccione los Tenant de origen y destino', ok: 'Next',
					parameters: [
						choice(name: 'TENANT_SOURCES', choices: ['DEV','TEST','PROD'].join('\n'), description: 'Seleccione un TENANT origen'),
						choice(name: 'TENANT_DESTINATION', choices: ['DEV','TEST','PROD'].join('\n'), description: 'Seleccione un TENANT destino')
					]
					env.TENANT_SOURCES = INPUT_PARAMS.TENANT_SOURCES
					env.TENANT_DESTINATION = INPUT_PARAMS.TENANT_DESTINATION
					
					
					echo "Selected --> TENANT_SOURCES: ${env.TENANT_SOURCES}, and TENANT_DESTINATION: ${env.TENANT_DESTINATION}"
					
					env.URL_SOURCE = env.TENANT_SOURCES == "PROD" ? env.URL_PROD : (env.TENANT_SOURCES == "DEV" ? env.URL_DEV : env.URL_TEST )
					echo "Selected ----> URL_SOURCE: ${env.URL_SOURCE}"
			
					env.ACCESS_TOKEN_SOURCE = env.TENANT_SOURCES == "PROD" ? env.ACCESS_TOKEN_PROD : (env.TENANT_SOURCES == "DEV" ? env.ACCESS_TOKEN_DEV : env.ACCESS_TOKEN_TEST )
					echo "Selected ----> ACCESS_TOKEN_SOURCE: ${env.ACCESS_TOKEN_SOURCE}"
			
					env.URL_DESTINATION = env.TENANT_DESTINATION == "PROD" ? env.URL_PROD : (env.TENANT_DESTINATION == "DEV" ? env.URL_DEV : env.URL_TEST )
					echo "Selected ------> URL_DESTINATION: ${env.URL_DESTINATION}"
			
					env.ACCESS_TOKEN_DESTINATION = env.TENANT_DESTINATION == "PROD" ? env.ACCESS_TOKEN_PROD : (env.TENANT_DESTINATION == "DEV" ? env.ACCESS_TOKEN_DEV : env.ACCESS_TOKEN_TEST )
					echo "Selected ------> ACCESS_TOKEN_DESTINATION: ${env.ACCESS_TOKEN_DESTINATION}"
					
				}
			}
		}
		stage("Select service") {
			steps {
				script{
					// Elimino la versión anterior del archivo de services para que no aparezcan repetidos
					sh "rm -f /tmp/service-list.txt"
					
					echo "Listado de servicios del SOURCE: INICIO"
					//sh "3scale service list https://${env.ACCESS_TOKEN_SOURCE}@${env.URL_SOURCE} --insecure"
					sh "3scale service list https://${env.ACCESS_TOKEN_SOURCE}@${env.URL_SOURCE} --insecure >> /tmp/service-list.txt"
					
					sh "cat /tmp/service-list.txt"
					echo "Listado de servicios del SOURCE: FIN"
					
					
					
					
//					sh "3scale service list https://${env.ACCESS_TOKEN_SOURCE}@${env.URL_SOURCE} --insecure >> /tmp/service-list.txt"
//					sh "3scale service list https://b81727d39397d3a8fca71bc64f25aee68fa29a816ac71cf32ba7641498430866@dev-3scale-admin.apps.osepext01.claro.amx --insecure >> /tmp/service-list.txt"
//					--------------------------------------------------------------------------
//					services=($(cat /tmp/service-list.txt | grep ID -v))
					
					// Groovy home: GROOVY_HOME=/usr/local/opt/groovy/libexec
					
					echo "-------------"
					sh "gawk -F: '{ print \$1 }' /etc/passwd"
					echo "-------------"
					sh "gawk -F: '{ print \$1 }' /tmp/service-list.txt"
					echo "----- OJOTA!! --------"
						
					
					// PRUEBAS EN MI MAQUINA
					// awk '{print $1 ":" $2}' /tmp/service-list.txt | grep ID -v
					// awk '{print "Id: " $1 ", Service: " $2}' /tmp/service-list.txt | grep ID -v		<======
					//
					//		
					// export SERVICES=$(awk '{print "Id: " $1 ", Service: " $2}' ./tmp/service-list.txt | grep ID -v)
					//
					// 		resultado, en una sola línea =>		 Id: 6, Service: Billetera Id: 11, Service: test
					
					
					sh "export services=(\$(gawk -F: '{ print \$1 : \$2 }' /tmp/service-list.txt | grep ID -v))"
					
					sh "length=${#services[@]}"
					sh "for ((i = 0; i != length; i++)); do echo "services $i: '${services[i]}'"; done;"
//					output
//						services 0: '6:Billetera'
//						services 1: '11:test'
//					
//					
//					output:
//						services 0: '6'
//						services 1: 'Billetera'
//						services 2: 'api'
//						services 3: '11'
//						services 4: 'test'
//						services 5: 'Test'
//					
//					
//					https://stackoverflow.com/questions/24890764/store-grep-output-in-an-array
//					--------------------------------------------------------------------------
//					targets=$(grep  -HR "pattern" . | cut -d: -f1)
//					length=${#targets[@]}
//					for ((i = 0; i != length; i++)); do
//					   echo "target $i: '${targets[i]}'"
//					done
//
//					awk -F "\"" '{print $2}' /tmp/file.txt
// 					awk -F "\"" '{print $2}' /tmp/service-list.txt
					
					
					def INPUT_PARAMS = input message: 'Seleccione el servicio a promocionar', ok: 'Next',
					parameters: [
						choice(name: 'ID_SOURCES', choices: ['6:Billetera'].join('\n'), description: "Seleccione un ID de servicio origen en ${env.TENANT_SOURCES}"),
						string(name: 'SERVICE_NAME', defaultValue: 'None', description: "Nombre del servicio a promocionar a ${env.TENANT_DESTINATION}")
					]
					env.ID_SOURCES = INPUT_PARAMS.ID_SOURCES
					env.SERVICE_NAME = INPUT_PARAMS.SERVICE_NAME
					
				}
			}
		}
		stage("Copy Service") {
			steps {
				script{
					echo "All parameters have been set as Environment Variables"
					def idS = env.ID_SOURCES.split(":")[0].trim()
					echo "ID_SOURCES: ${idS}"
					echo "SERVICE_NAME: ${env.SERVICE_NAME}"
			
//					def URL_SOURCE = env.TENANT_SOURCES == "PROD" ? env.URL_PROD : (env.TENANT_SOURCES == "DEV" ? env.URL_DEV : env.URL_TEST )
//					echo "URL_SOURCE: ${URL_SOURCE}"
//			
//					def ACCESS_TOKEN_SOURCE = env.TENANT_SOURCES == "PROD" ? env.ACCESS_TOKEN_PROD : (env.TENANT_SOURCES == "DEV" ? env.ACCESS_TOKEN_DEV : env.ACCESS_TOKEN_TEST )
//					echo "ACCESS_TOKEN_SOURCE: ${ACCESS_TOKEN_SOURCE}"
//			
//					def URL_DESTINATION = env.TENANT_DESTINATION == "PROD" ? env.URL_PROD : (env.TENANT_DESTINATION == "DEV" ? env.URL_DEV : env.URL_TEST )
//					echo "URL_DESTINATION: ${URL_DESTINATION}"
//			
//					def ACCESS_TOKEN_DESTINATION = env.TENANT_DESTINATION == "PROD" ? env.ACCESS_TOKEN_PROD : (env.TENANT_DESTINATION == "DEV" ? env.ACCESS_TOKEN_DEV : env.ACCESS_TOKEN_TEST )
//					echo "ACCESS_TOKEN_DESTINATION: ${ACCESS_TOKEN_DESTINATION}"
			
					sh "3scale copy service ${idS} --source=https://${ACCESS_TOKEN_SOURCE}@${URL_SOURCE} --destination=https://${ACCESS_TOKEN_DESTINATION}@${URL_DESTINATION} --target_system_name=${env.SERVICE_NAME} --insecure"
					
				}
			}
			
			
		}
	}
}