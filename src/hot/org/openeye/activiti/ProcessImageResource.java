package org.openeye.activiti;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.web.AbstractResource;

@Scope(APPLICATION)
@Name("processImageResource")
@BypassInterceptors
@Install(precedence = BUILT_IN)
public class ProcessImageResource extends AbstractResource {
	
	@Override
	public void getResource(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		byte bytes[] = null;
		ServletLifecycle.beginRequest(request);

		// Get deployment id
		String path = request.getPathInfo();
		int begin = path.lastIndexOf("/");
		int end = path.lastIndexOf(".");		
		String id = path.substring(begin + 1, end);
		
		try {
			Transaction.instance().begin();
		} catch (NotSupportedException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}

		// Get process image
		//bytes = getProcessImage(id,archive);
		bytes = getProcessImage(id);

		if (bytes == null)
			return;

		try {

		} finally {
			try {
				Transaction.instance().commit();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (RollbackException e) {
				e.printStackTrace();
			} catch (HeuristicMixedException e) {
				e.printStackTrace();
			} catch (HeuristicRollbackException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			ServletLifecycle.endRequest(request);
		}

		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpg");
		response.getOutputStream().write(bytes);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	@Override
	public String getResourcePath() {
		return "/processImage";
	}

	/**
	 * @return the process image
	 */
	private byte[] getProcessImage(String deploymentId) {

		byte bytes[] = null;

		if ((deploymentId != null) && !deploymentId.isEmpty()) {

			// Get Process Engine
			ProcessEngineService processEngineService = (ProcessEngineService) Component
					.getInstance(ProcessEngineService.class);
			
			// Get the resourcename
			String resource = processEngineService.getRepositoryService().createDeploymentQuery().deploymentId(deploymentId).singleResult().getName();

			int begin = 0;
			int end = resource.lastIndexOf(".");
			resource = resource.substring(begin, end) + "/processimage.jpg";			
			
			// Get the input stream for the process image
			InputStream is = processEngineService.getRepositoryService()
					.getResourceAsStream(deploymentId, resource);

			if (is != null) {

				try {

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					int next = is.read();
	
					while (next > -1) {
						bos.write(next);
						next = is.read();
					}

					bos.flush();
					bytes = bos.toByteArray();

				} catch (Exception e) {
					// TODO
				}
			}
		}

		// Return null if no process image form was found
		return bytes;
	}

}
