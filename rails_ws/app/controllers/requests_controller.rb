class RequestsController < ApplicationController
  # GET /requests
  # GET /requests.json
  def index
    @requests = Request.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @requests }
    end
  end

  # GET /requests/1
  # GET /requests/1.json
  def show
    @request = Request.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @request }
    end
  end

  # GET /requests/new
  # GET /requests/new.json
  def new
    @request = Request.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @request }
    end
  end

  # GET /requests/1/edit
  def edit
    @request = Request.find(params[:id])
  end

  # POST /requests
  # POST /requests.json
  def create
    @request = Request.new(params[:request])

    registration_ids = []
    @request.num_phones = 0

    PhoneRegistration.all.each { |p|
      if p.device_ident != @request.requester_device_ident
       registration_ids.push(p.registration_ident)
       @request.num_phones = @request.num_phones + 1
      end
    }

    @request.status = "active"
    @request.num_errors = 0
    id  = Request.all.size + 1


    gcm = GCM.new("api_key")
    options = {data: {program: @request.intent_program, request_id: id}}
#    response = gcm.send_notification(registration_ids, options)

    respond_to do |format|
      if @request.save
        format.html { redirect_to @request, notice: 'Request was successfully created.' }
        format.json { render json: @request, status: :created, location: @request }
      else
        format.html { render action: "new" }
        format.json { render json: @request.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /requests/1
  # PUT /requests/1.json
  def update
    @request = Request.find(params[:id])

    registration_ids = []

    PhoneRegistration.all.each { |p|
      if p.device_ident == @request.requester_device_ident
        registration_ids = [p.registration_ident]
        break
      end
    }

    if registration_ids == []
      raise Exception, "No matching registration id for device id"
    end

    id = params[:id]
    r_num_errors = @request.num_errors
    params[:request]["num_errors"] = @request.num_errors
    status = @request.status

    if params[:request]["output"].empty? == false and @request.status = "active"
      params[:request]["status"] = "success"
      status = params[:request]["status"]
      output = params[:request]["output"]

      gcm = GCM.new("api_key")
      options = {data: {output: output, request_id: id}}
#      response = gcm.send_notification(registration_ids, options)
    elsif params[:request]["error"].empty? == false and @request.status == "active"
      params[:request]["num_errors"] = params[:request]["num_errors"].to_i + 1
      r_num_errors = params[:request]["num_errors"]  

      gcm = GCM.new("api_key")
      options = {data: {error: @request.error, request_id: id}}
      response = gcm.send_notification(registration_ids, options)
#      
      if params[:request]["num_errors"] == @request.num_phones
        params[:request]["status"] = "error"
        status = "error"
      end
    end

    params[:request][:requester_device_ident] = @request.requester_device_ident
    params[:request][:intent_program] = @request.intent_program
    params[:request][:num_phones] = @request.num_phones
    params[:request][:num_errors] = r_num_errors
    params[:request][:status] = status


    respond_to do |format|
      if @request.update_attributes(params[:request])
        format.html { redirect_to @request, notice: 'Request was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @request.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /requests/1
  # DELETE /requests/1.json
  def destroy
    @request = Request.find(params[:id])
    @request.destroy

    respond_to do |format|
      format.html { redirect_to requests_url }
      format.json { head :no_content }
    end
  end
end
