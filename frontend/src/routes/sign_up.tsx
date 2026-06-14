import { createFileRoute, Link } from '@tanstack/react-router'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'

export const Route = createFileRoute('/sign_up')({
  component: SignUpComponent,
})

function SignUpComponent() {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [agreeToTerms, setAgreeToTerms] = useState(false)

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    console.log({ name, email, password, agreeToTerms })
  }

  return (
    <div className="min-h-screen bg-[#f4f7f9] flex flex-col justify-between items-center font-sans antialiased py-12 px-4">
      
      {/* Top Header Section */}
      <div className="text-center mt-8">
        <h1 className="text-3xl font-bold text-[#004094] tracking-tight">
          Betelgeuse
        </h1>
        <p className="text-sm text-gray-500 mt-1 font-medium">
          Secure Enterprise Access Control
        </p>
      </div>

      {/* Main Authentication Card */}
      <div className="w-full max-w-[440px] bg-white rounded-xl shadow-[0_4px_24px_rgba(0,0,0,0.06)] overflow-hidden border border-gray-100 flex flex-col my-auto">
        <div className="p-8 pb-6">
          <h2 className="text-2xl font-bold text-gray-900">Create Account</h2>
          <p className="text-sm text-gray-500 mt-1">Register your enterprise identity profile</p>

          <form onSubmit={handleSubmit} className="mt-6 space-y-4">
            {/* Name Input */}
            <div className="space-y-1.5">
              <Label htmlFor="name" className="text-xs font-bold text-gray-500 uppercase tracking-wider">
                Full Name
              </Label>
              <Input
                id="name"
                type="text"
                required
                placeholder="John Doe"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="h-10 border-gray-300 focus-visible:ring-[#004094] focus-visible:border-[#004094]"
              />
            </div>

            {/* Email Input */}
            <div className="space-y-1.5">
              <Label htmlFor="email" className="text-xs font-bold text-gray-500 uppercase tracking-wider">
                Email Address
              </Label>
              <Input
                id="email"
                type="email"
                required
                placeholder="name@company.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="h-10 border-gray-300 focus-visible:ring-[#004094] focus-visible:border-[#004094]"
              />
            </div>

            {/* Password Input */}
            <div className="space-y-1.5">
              <Label htmlFor="password" className="text-xs font-bold text-gray-500 uppercase tracking-wider">
                Password
              </Label>
              <Input
                id="password"
                type="password"
                required
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="h-10 border-gray-300 focus-visible:ring-[#004094] focus-visible:border-[#004094]"
              />
            </div>

            {/* Terms and Conditions Checkbox */}
            <div className="flex items-start space-x-2 pt-1">
              <Checkbox
                id="terms-agree"
                checked={agreeToTerms}
                onCheckedChange={(checked) => setAgreeToTerms(!!checked)}
                className="border-gray-300 mt-0.5 text-[#004094] data-[state=checked]:bg-[#004094] data-[state=checked]:border-[#004094]"
              />
              <Label htmlFor="terms-agree" className="text-xs text-gray-600 font-normal leading-normal cursor-pointer select-none">
                I agree to the access policies and data handling declarations.
              </Label>
            </div>

            {/* Submit Button */}
            <Button
              type="submit"
              className="w-full mt-2 h-11 bg-[#004094] hover:bg-[#003376] text-white font-medium text-sm transition shadow-sm gap-2"
            >
              Sign Up
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
              </svg>
            </Button>
          </form>

          {/* Sign In Redirect Option */}
          <div className="mt-6 text-center text-sm text-gray-500 border-t border-gray-100 pt-5">
            Already have an account?{' '}
            <Link to="/sign_in" className="font-bold text-[#004094] hover:underline">
              Sign in
            </Link>
          </div>
        </div>

        {/* Card Footer Security Note */}
        <div className="bg-[#f8fafc] border-t border-gray-100 px-8 py-3.5 flex items-center justify-center gap-2 text-xs text-gray-500 font-medium">
          <svg className="w-3.5 h-3.5 text-gray-400" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
            <path fillRule="evenodd" d="M2.166 4.999A11.954 11.954 0 0010 1.944a11.954 11.954 0 007.834 3.056 10.03 10.03 0 01-1.353 6.945c-.463.717-1.036 1.36-1.706 1.914a13.385 13.385 0 01-4.22 2.368 1 1 0 01-.555 0 13.385 13.385 0 01-4.22-2.368 10.03 10.03 0 01-1.353-6.945zM10 5a1 1 0 00-.707.293l-3 3a1 1 0 001.414 1.414L9 8.414v4a1 1 0 102 0v-4l1.293 1.293a1 1 0 001.414-1.414l-3-3A1 1 0 0010 5z" clipRule="evenodd" />
          </svg>
          End-to-end encrypted authentication
        </div>
      </div>

      {/* Footer Navigation */}
      <div className="flex justify-center gap-6 text-xs text-gray-500 font-medium pb-4">
        <a href="#privacy" className="hover:underline">Privacy Policy</a>
        <a href="#terms" className="hover:underline">Terms of Service</a>
        <a href="#security" className="hover:underline">Security Disclosure</a>
      </div>

    </div>
  )
}