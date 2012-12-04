class PhoneRegistrationsController < ApplicationController
  # GET /phone_registrations
  # GET /phone_registrations.json
  def index
    @phone_registrations = PhoneRegistration.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @phone_registrations }
    end
  end

  # GET /phone_registrations/1
  # GET /phone_registrations/1.json
  def show
    @phone_registration = PhoneRegistration.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @phone_registration }
    end
  end

  # GET /phone_registrations/new
  # GET /phone_registrations/new.json
  def new
    @phone_registration = PhoneRegistration.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @phone_registration }
    end
  end

  # GET /phone_registrations/1/edit
  def edit
    @phone_registration = PhoneRegistration.find(params[:id])
  end

  # POST /phone_registrations
  # POST /phone_registrations.json
  def create
    @phone_registration = PhoneRegistration.new(params[:phone_registration])

    PhoneRegistration.all.each { |a|
      if a.device_ident == @phone_registration.device_ident
      a.destroy
        break
      end
    }

    respond_to do |format|
      if @phone_registration.save
        format.html { redirect_to @phone_registration, notice: 'Phone registration was successfully created.' }
        format.json { render json: @phone_registration, status: :created, location: @phone_registration }
      else
        format.html { render action: "new" }
        format.json { render json: @phone_registration.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /phone_registrations/1
  # PUT /phone_registrations/1.json
  def update
    @phone_registration = PhoneRegistration.find(params[:id])

    respond_to do |format|
      if @phone_registration.update_attributes(params[:phone_registration])
        format.html { redirect_to @phone_registration, notice: 'Phone registration was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @phone_registration.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /phone_registrations/1
  # DELETE /phone_registrations/1.json
  def destroy
    @phone_registration = PhoneRegistration.find(params[:id])
    @phone_registration.destroy

    respond_to do |format|
      format.html { redirect_to phone_registrations_url }
      format.json { head :no_content }
    end
  end
end
