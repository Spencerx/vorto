package org.eclipse.vorto.codegen.spi.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.vorto.codegen.spi.model.Generator;
import org.eclipse.vorto.codegen.spi.repository.GeneratorRepository;
import org.eclipse.vorto.codegen.spi.service.VortoService;
import org.eclipse.vorto.codegen.spi.utils.GatewayUtils;
import org.eclipse.vorto.repository.api.IModelRepository;
import org.eclipse.vorto.repository.client.RepositoryClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.google.common.base.Strings;

public abstract class AbstractGeneratorConfiguration implements ApplicationRunner, EnvironmentAware, IGeneratorConfiguration  {

	private static final String LOCALHOST = "localhost";
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGeneratorConfiguration.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private GeneratorRepository generatorRepo;
	
	@Autowired
	private VortoService vorto;

	@PostConstruct
	public void init() {
		ignoreHostnameVerification();
		ignoreSSLCertificateVerification();
	}
	
	public void ignoreHostnameVerification() {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession sslSession) {
				if (hostname.equals(LOCALHOST)) {
					return true;
				}
				return false;
			}
		});
	}
	
	public void ignoreSSLCertificateVerification() {
		SSLContext ctx = null;
        TrustManager[] trustAllCerts = new X509TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers(){return null;}
            
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            	// Do nothing
            }
            
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // Do nothing	
            }
        }};
        
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, trustAllCerts, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
        	LOGGER.error("Error in ignoring SSL Ceritificate Verification", e);
        }

        SSLContext.setDefault(ctx);
	}
	
	public String getVortoRepoUrl() {
		String vortoRepoUrl = System.getenv("VORTO_REPO_URL");
		if (!Strings.nullToEmpty(vortoRepoUrl).trim().isEmpty()) {
			return vortoRepoUrl;
		}
		
		String vortoServerUrl = env.getProperty("vorto.serverUrl");
		if (!Strings.nullToEmpty(vortoServerUrl).trim().isEmpty()) {
			return vortoServerUrl;
		}
		
		throw GatewayUtils.notFound("Not able to get the Vorto Server URL both from the " + 
				"Environment variable VORTO_REPO_URL or the config setting 'vorto.serverUrl'").get();
	}

	public String getAppServiceUrl() {
		String serviceoUrl = System.getenv("APP_SERVICE_URL");
		if (!Strings.nullToEmpty(serviceoUrl).trim().isEmpty()) {
			return serviceoUrl;
		}
		
		String applicationServiceUrl = env.getProperty("server.serviceUrl");
		if (!Strings.nullToEmpty(applicationServiceUrl).trim().isEmpty()) {
			return applicationServiceUrl;
		}
		
		throw GatewayUtils.notFound("Not able to get the Application Service URL both from the " + 
				"Environment variable APP_SERVICE_URL or the config setting 'server.serviceUrl'").get();
	}

	@Override
	public void setEnvironment(Environment env) {
		this.env = env;
	}

	protected abstract void doSetup();
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		try {
			doSetup();
			generatorRepo.list().stream().forEach(GatewayUtils.checkEnvModifications(env));
			generatorRepo.list().stream().forEach(vorto::register);
			
		} catch(RuntimeException e) {
			LOGGER.error("Error registering generators", e);
		}
	}
	
	protected void addGenerator(Generator generator) {
		generatorRepo.add(generator);
	}
	
	
	@Bean
	public IModelRepository modelRepository() {
		return RepositoryClientBuilder.newBuilder().setBaseUrl(getVortoRepoUrl()).setTenant(env.getProperty("vorto.tenantId")).buildModelRepositoryClient();
	}
}
